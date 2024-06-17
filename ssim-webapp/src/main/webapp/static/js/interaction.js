// so logging won't cause errors when console is unavailable
if (typeof console == 'undefined') {
	var console = {
		log: function () {},
		warn: function () {}
	};
}

var related = [],
	encounters = [],
	baseEncounterName = '',
	encounterNameField = '',
	searchQuery = {},
	validationRules = {
		// don't ignore hidden fields
		ignore: '',
		// custom rules
		rules: {
			"encounter-num-participants": {
				required: function (field) {
					var encounterNum = $(field).closest('form').attr('data-encounter-num'),
						participants = encounters[encounterNum].participants;
					return participants.length > 0;
				}
			}
		},
		// focus the first invalid field
		invalidHandler: function(form, validator) {
			var element = $(validator.errorList[0].element);
			if (element.is(':visible')) element.focus();
			else window.scrollTo(0, element.parent().offset().top);

			var msg = $('<div/>');
			if (validator.errorList.length == 1) msg.text('There is an error on this page, please correct it to continue.');
			else msg.text('There are errors on this page, please correct them to continue.');
			$(msg).dialog({
				resizable: false,
				modal: true,
				buttons: {
					'Ok': function () {
						$(this).dialog('close');
					}
				}
			});
		},
		// put errors on radio buttons below all of them
		errorPlacement: function (error, element) {
			if (element.is('input[type="radio"]')) {
				element.closest('fieldset').append((error));
			} else {
				element.after(error);
			}
			element.closest('.form-row, .list-row').addClass('error-row');
		},
		// remove error-row class when field validates
		success: function (label) {
			label.closest('.form-row, .list-row').removeClass('error-row');
			label.remove();
		}
	};
$(document).ready(function () {
	var templatesReady = false,
		templates = null,
		noTemplateError = new Error("No template found"),
		query = loadQueryString(),
		editing = (typeof query.name != 'undefined');
	// preload the html templates
	$.get($('body').attr('data-templates'), function (data, status, xhr) {
		templates = $(data);
		templatesReady = true;
	});
	if (typeof $().validate == 'function') {
		$.validator.addMethod("time", function(value, element) {
			var reg = /^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?( )?((AM)|(am)|(PM)|(pm))?$/i;
			return this.optional(element) || reg.test($.trim(value));
		}, "Please enter a valid time.");
		$.validator.addMethod("duration", function(value, element) {
			var reg = /^([0-9]*)(:([0-5][0-9])){1,2}$/i;
			return this.optional(element) || reg.test($.trim(value));
		}, "Please enter a valid time.");
	}
	function loadFragment (id, callback, replacements, tries) {
		var maxTries = 10,
			interval = 50;	// try to load the template every 50ms for a max 10 times (500ms total)
		if (typeof replacements == 'undefined') replacements = {};
		if (typeof tries == 'undefined') tries = 0;

		if (templatesReady) {
			var template = templates.find('#' + id),
				holder = $('<div>');
			if (template.length > 0) template = template.first();
			else throw noTemplateError;
			holder.append(template.clone().children());
			callback($(tplReplace(holder.html(), replacements)));
		} else if (tries < maxTries) {
			setTimeout(function () { loadFragment(id, callback, replacements, tries + 1); }, interval);
		} else {
			throw noTemplateError;
		}
	}

	function tplReplace (tpl) {
		for (var i=1,l=arguments.length;i<l;i++) {
			var replacement = arguments[i];
			if (typeof replacement == 'String') {
				var r = new RegExp('{' + i + '}','g');
				tpl = tpl.replace(r, replacement);
			} else if (typeof replacement == "object") {
				for (var k in replacement) {
					var r = new RegExp('{' + k + '}', 'g');
					tpl = tpl.replace(r, replacement[k]);
				}
			}
		}
		return tpl;
	}

	function loadQueryString () {
		var str = window.location.search.substring(1),	// query with ? cut off the front
			arr = str.split('&'),
			query = {};
		for (var i = 0, l = arr.length; i < l; i++) {
			var t = arr[i].split('=');
			query[t[0]] = t[1];
		}
		return query;
	}

	function showLoader (elName) {
		$(elName).append('<div class="loader"></div>');
	}
	function hideLoader (elName) {
		$(elName).children('.loader').remove();
	}

	function storeFields (form) {
		var tempVals = form.serializeArray(),
			vals = {};
		for (var i = 0, l = tempVals.length; i < l; i++) {
			var multiPos = tempVals[i].name.search(/-[0-9]+$/);
			if (multiPos > -1) {
				multiName = tempVals[i].name.substring(0, multiPos);
				if (!(vals[multiName] instanceof Array)) vals[multiName] = [];
				vals[multiName].push(tempVals[i].value);	// position of entries is not guaranteed to be preserved
			} else {
				if (typeof vals[tempVals[i].name] != 'undefined') {
					// handle checkboxes
					if (vals[tempVals[i].name] instanceof Array) {
						vals[tempVals[i].name].push(tempVals[i].value);
					} else{
						vals[tempVals[i].name] = [
							vals[tempVals[i].name],
							tempVals[i].value
						];
					}
				} else {
					vals[tempVals[i].name] = tempVals[i].value;
				}
			}
		}
		// fix multi-sections with multiple repeating components
		form.find('.multi-section, .field-match-row').each(function (index, multiSection) {
			multiSection = $(multiSection);
			var children = multiSection.find('.multi'),
				firstFields = children.first().find(':input').not('button');
			if (firstFields.length > 1) {
				// add the group name to the data
				var groupName = multiSection.attr('data-grouped-multi-name');
				vals[groupName] = [];
				children.each(function(groupIndex, group) {
					var groupObject = {};
					$(group).find(':input').not('button').each(function (fieldIndex, field) {
						field = $(field);
						var fieldname = field.attr('name'),
							baseFieldname = (fieldname.search(/-[0-9]+$/) > 0) ? fieldname.substring(0, fieldname.search(/-[0-9]+$/)) : fieldname;
						// take the individual arrays out
						if (baseFieldname != groupName) vals[baseFieldname] = undefined;
						// put the value into the group object
						groupObject[baseFieldname] = field.val();
					});
					vals[groupName].push(groupObject);
				});
			}
		});
		return vals;
	}

	function addMultiField (multiSection, removable, callback) {
		var currentFields = multiSection.find('.multi'),
			numFields = currentFields.length,
			fieldname = currentFields.eq(0).find(':input').first().attr('name'),
			baseFieldname = fieldname.substring(0, fieldname.lastIndexOf('-'));
		if (typeof removable == 'undefined') removable = true;
		loadFragment(multiSection.attr('data-multi-template'), function (newField) {
			// add a remove button
			if (removable) {
				loadFragment('remove-template', function (remove) {
					remove.button({icons: {primary: "ui-icon-circle-close"}, text: false});
					remove.attr('data-multi-index', numFields);
					remove.click(function (event) {
						event.preventDefault();
						var button = $(event.target);
						removeMultiField(button.closest('.multi-section'), button.attr('data-multi-index'));
					});
					newField.append(remove);
					// if there was only one field before, it couldn't be removed before but can now so add a "remove" button
					if (currentFields.length == 1) {
						var firstRemove = remove.clone(true).attr('data-multi-index', 0);
						currentFields.eq(0).append(firstRemove);
					}
				});
			}
			// add the new field
			currentFields.last().after(newField);
			newField.trigger('templateLoaded');
			if (typeof callback == 'function') callback(newField);
		}, {num:numFields,fieldname:baseFieldname});
	}

	function removeMultiField (multiSection, removeIndex) {
		var fields = multiSection.find('.multi');
		// update the field names to reflect their new positions (we don't want any duplicates if we remove one in the middle then add another later)
		fields.each(function (index, el) {
			el = $(el);
			if (index > removeIndex) {
				var r = new RegExp('-([0-9]+)$', 'g'),
					inputs = el.find('[name]'),
					labels = el.find('[for]'),
					remove = el.find('.remove');
				inputs.each(function (input_index, input_el) {
					// Replace name and id with one less. Done separately because they might not be the same.
					input_el = $(input_el);
					input_el.attr('name', input_el.attr('name').replace(r, '-' + (index - 1)));
					input_el.attr('id', input_el.attr('id').replace(r, '-' + (index - 1)));
				});
				labels.each(function (label_index, label_el) {
					label_el = $(label_el);
					label_el.attr('for', label_el.attr('for').replace(r, '-' + (index - 1)));
				});
				remove.attr('data-multi-index', remove.attr('data-multi-index') - 1);
			}
		});
		// remove the field
		fields.eq(removeIndex).remove();
		// if we stared with two fields (and now have one), remove the "remove" button so they can't remove the last one
		if (fields.length == 2) {
			fields.find('.remove').remove();
		}
	}

	function addAndOr (row, andOrVal, callback) {
		var andor = row.find('.andor'),
			rowNum = parseInt(row.attr('data-multi-num'),10),
			newRow = row.clone(),
			section = row.closest('.andor-section'),
			fragment = 'andor-template';
		// add the new row
		newRow.find('input').val('');
		newRow.find('.andor > *').hide();
		if (andOrVal == 'and') newRow.find('.and').show();
		else if (andOrVal == 'or') newRow.find('.or').show();
		row.after(newRow);
		andor.hide();

		// load and/or select or text as appropriate
		if (rowNum > 0) {
			if (andOrVal == 'and') fragment = 'and-template';
			else if (andOrVal == 'or') fragment = 'or-template';
		} else if (andor.find('.or').length == 0) {
			fragment = 'and-template';
		} else if (andor.find('.and').length == 0) {
			fragment = 'or-template';
		}
		loadFragment(fragment, function (select) {
			andor.after(select);
			if (select.is('select')) {
				if (andOrVal == 'and') select.val('and');
				else if (andOrVal == 'or') select.val('or');
			}
			select.trigger('templateLoaded');
		}, {'multinum':rowNum});

		// update row numbers
		section.find('.multi').each(function (rowIndex, rowEl) {
			if (rowIndex > rowNum) {
				rowEl = $(rowEl);
				rowEl.attr('data-multi-num', rowIndex);
				rowEl.find(':input').each(function (fieldIndex, fieldEl) {
					fieldEl = $(fieldEl);
					if (!fieldEl.is('[name]')) return;
					fieldEl.attr('name', fieldEl.attr('name').replace(/-[0-9]+$/, '-' + rowIndex));
				});
			}
		});

		// add a remove button
		loadFragment('remove-template', function (remove) {
			// add elements
			remove.button({icons: {primary: "ui-icon-circle-close"}, text: false});
			var newRowRemove = remove.clone();
			row.append(remove);
			newRow.append(newRowRemove);
			// trigger events
			remove.trigger('templateLoaded');
			newRowRemove.trigger('templateLoaded');
		});

		if (typeof callback == 'function') callback(newRow);
	}

	function removeAndOr (removeRow) {
		var removeIndex = removeRow.attr('data-multi-num'),
			multiSection = removeRow.closest('.andor-section');
		removeRow.remove();
		// make sure the and/or buttons are visible on new last row and select or buttons are visible on new first row
		var rows = multiSection.find('.multi'),
			firstRow = rows.first(),
			lastRow = rows.last();
		if (removeIndex == 0 && rows.length > 1) {
			if (firstRow.find('.andor .and').length != 0 &&
				firstRow.find('.andor .or').length != 0) {
				loadFragment('andor-template', function (select) {
					var oldAndor = firstRow.find('.andor-select'),
						oldVal = oldAndor.find(':input').val();
					oldAndor.remove();
					firstRow.append(select);
					select.val(oldVal);
				});
			}
		} else {
			lastRow.find('.andor-select').remove();
			lastRow.find('.andor').show();
			if (rows.length == 1) lastRow.find('.andor').children().show();
		}
		// if only one row is left, no remove button
		if (rows.length == 1) lastRow.find('.remove').remove();
		// update the ids of the remaining fields
		rows.each(function (index, el) {
			el = $(el);
			var r = new RegExp('-([0-9]+)$', 'g'),
				inputs = el.find('[name]');
			inputs.each(function (input_index, input_el) {
				// Replace name and id with one less. Done separately because they might not be the same.
				input_el = $(input_el);
				input_el.attr('name', input_el.attr('name').replace(r, '-' + index));
			});
			el.attr('data-multi-num', index);
		});
	}

	function fillField (name, value, form, tries) {
		var field = form.find('[name=' + name + ']');
		if (value instanceof Array) {
			// it's a multi-field
			if (value.length > 0 && typeof value[0] == 'object') {
				// repeating sets of multiple fields (e.g. stimulated recall interviews)
				var group = form.find('[data-grouped-multi-name="' + name + '"]');
				if (group.length > 0) {
					var multi = function (i) {
						// this is pulled out so the scope can maintain the correct value of i
						if (group.find('.andor-section').length == 0) {
							addMultiField(group, true, function (fieldset) {
								for (var key in value[i]) {
									fillField(key+'-'+i, value[i][key], form);
								}
							});
						} else {
							addAndOr(group.find('.multi').eq(i - 1), value[0].andor, function (fieldset) {
								for (var key in value[i]) {
									fillField(key+'-'+i, value[i][key], form);
								}
							});
						}
					};
					var difference = value.length - group.find('.multi').length;
					for (var i = 0, l = value.length; i < l; i++) {
						if (i > (l - (difference + 1))) {
							multi(i);
						} else {
							for (var key in value[i]) {
								fillField(key+'-'+i, value[i][key], form);
							}
						}
					}
					// remove extra rows
					if (difference < 0) {
						for (i = difference; i < 0; i++) {
							if (group.find('.andor-section').length == 0) {
								removeMultiField(group, group.find('.multi').length - 1);
							} else {
								removeAndOr(group.find('.multi').last());
							}
						}
					}
				} else {
					if (typeof console != 'undefined') console.warn('Unable to find group: ' + name);
				}
			} else {
				field = form.find('[name=' + name + '-0]');
				var existingFields = field.length;
				if (existingFields > 0) {
					var multiSection = field.closest('.multi-section');
					for (var i = 0, l = value.length; i < l; i++) {
						if (i >= existingFields) {
							addMultiField(multiSection, true, function (field) {
								fillField(name+'-'+i, value[i], multiSection);
							});
						} else {
							fillField(name+'-'+i, value[i], multiSection);
						}
					}
				} else {
					// wrong number of multi-fields, should not get here
					if (typeof console != 'undefined') console.warn('Wrong number of multi-fields for: ' + name);
				}
			}
		} else if (field.length > 1) {
			// checkboxes and radio buttons
			var first = field.first().attr('type');
			if (first == 'radio' || first == 'checkbox') {
				field.each(function (index, f) {
					f = $(f);
					if (f.attr('value') == value) f.attr('checked', 'checked');
				});
			}
		} else if (field.length == 1) {
			if (field.is('select')) {
				var options = field.find('option');
				options.each(function (index, opt) {
					opt = $(opt);
					if (opt.val() == value) opt.attr('selected', 'selected');
				});
			} else if (field.is('[type="file"]')) {
				// we can't just set this, wait a moment for the enhance function to finish replacing it with flash and try again
				if (typeof tries == 'undefined') tries = 1;
				if (tries < 10) {
					setTimeout(function () {
						fillField(name, value, form, tries+1);
					}, 100);
				} else {
					// uploader not found
					if (typeof console != 'undefined') console.warn('Unable to set value for uploaded file: ' + name);
						}
			} else if (field.siblings('.uploadify').length > 0) {
				if ($.trim(value) !== '') {
					showUploadedFile(value, $('<div class="upload-status">' + value + '</div>'), field.parent());
				}
			} else {
				// input, textarea
				field.val(value);
			}
		} else {
			// field not found, should not get here
			if (typeof console != 'undefined') console.warn('Trying to fill a field that doesn\'t exist: ' + name);
		}
	}

	function showUploadedFile (filename, msg, parent) {
		parent.find('.upload-status').remove();
		parent.find('.uploadify-button').hide();
		parent.find('.uploadify-queue').hide();
		parent.find('.uploadify').css({
			position:'absolute',
			left:'-9999px'
		});
		parent.append(msg);
		loadFragment('remove-template', function (remove) {
			remove.button({icons: {primary: "ui-icon-circle-close"}, text: true});
			remove.removeAttr('data-multi-index');
			remove.click(function (event) {
				event.preventDefault();
				// hide the old message and empty the field
				parent.find('.upload-status').remove();
				parent.find('input[type="hidden"]').attr('value', '');
				// add a message, then hide it after a bit
				var removeMsg = $('<div class="upload-status upload-error">File removed</div>');
				parent.find('.uploadify').before(removeMsg);
				setTimeout(function() {
					removeMsg.hide('fast', function () {
						$(this).remove();
						// make the button and queue visible again
						parent.find('.uploadify-button').show();
						parent.find('.uploadify-queue').empty().show();
						parent.find('.uploadify').css({
							position:'relative',
							left:'auto'
						});
					});
				}, 3000);
			});
			msg.append(remove);
		});
		// add a hidden input field with the file name
		parent.find('input[type="hidden"]').attr('value', filename);
	}

	function listParticipants (encounterNum, participantList) {
		var encounter = encounters[encounterNum],
			participants = encounter.participants;
		participantList.empty();
		for (var i = 0, l = participants.length; i < l; i++) {
			// get the participant's codename
			var codename = "(Unnamed participant)";
			if (participants[i].codename !== '') codename = participants[i].codename;
			// add the participant to the list
			loadFragment('participant-list-template', function (listItem) {
				participantList.append(listItem);
				// listItem.find('.remove-participant-metadata').button({icons: {primary: "ui-icon-circle-close"}, text: true});
				// listItem.find('.edit-participant-metadata').button({icons: {primary: "ui-icon-pencil"}, text: true});
			}, {enc:parseInt(encounterNum)+1,par:i+1,codename:codename});
		}
	}

	function listEncounters (encounterList) {
		encounterList.empty();
		var callback = function (listItem) {
			encounterList.append(listItem);
			// listItem.find('.remove-encounter-metadata').button({icons: {primary: "ui-icon-circle-close"}, text: true});
			// listItem.find('.edit-encounter-metadata').button({icons: {primary: "ui-icon-pencil"}, text: true});
		};
		for (var i = 0, l = encounters.length; i < l; i++) {
			loadFragment('encounter-list-template', callback, {num:i+1});
		}
		if (encounters.length > 0) {
			encounterList.parent().find('.has-encounters').show();
			encounterList.parent().find('.no-encounters').hide();
		} else {
			encounterList.parent().find('.has-encounters').hide();
			encounterList.parent().find('.no-encounters').show();
		}
		// re-validating doesn't work properly, just remove error messages
		if (encounters.length > 0) {
			var row = encounterList.closest('.list-row');
			row.removeClass('error-row');
			row.find('.error').remove();
		}
	}

	function sendAutocompletes(form) {
		form.find('.autocomplete').each(function (index, field) {
			field = $(field);
			var url = field.attr('data-autocomplete-save'),
				param = field.attr('data-autocomplete-param'),
				value = field.val(),
				data = {};
			if ($.trim(value) !== '') {
				data[param] = {value:value};
				$.ajax({
					type: 'POST',
					url: url,
					contentType: 'application/json',
					dataType: 'json',
					data: JSON.stringify(data)
				});	// don't need to do anything on success or failure
			}
		});
	}

	function storeSearchConstraints (searchSection) {
		var temp = $('<form>'),	// temporary form so we can use serializeArray
			prev, parent,
			prepare = function (section) {
				prev = section.prev();
				parent = section.parent();
				temp.append(section);
			},
			restore = function (section) {
				if (prev.length > 0) prev.after(section);
				else parent.prepend(section);
			};
		// add file name search
		if (searchSection.hasClass('search-filename')) {
			prepare(searchSection);
			searchQuery.filename = storeFields(temp);
			restore(searchSection);
		}
		// add transcript search
		if (searchSection.hasClass('search-transcripts')) {
			searchQuery.transcript = [];
			searchSection.find('.sub').each(function (index, turn) {
				turn = $(turn);
				prepare(turn);
				searchQuery.transcript.push(storeFields(temp));
				restore(turn);
			});
		}
		// add encounter search
		if (searchSection.hasClass('search-encounter-metadata')) {
			prepare(searchSection);
			searchQuery.encounter = storeFields(temp);
			restore(searchSection);
		}
		// add participant search
		if (searchSection.hasClass('search-participant-metadata')) {
			searchQuery.participants = [];
			searchSection.find('.sub').each(function (index, turn) {
				turn = $(turn);
				prepare(turn);
				searchQuery.participants.push(storeFields(temp));
				restore(turn);
			});
		}
	}

	function listSearchConstraints (place) {
		var button,
			modifiable = false,
			filenameSection = $('section.search-filename'),
			transcriptsSection = $('section.search-transcripts'),
			encounterSection = $('section.search-encounter-metadata'),
			participantsSection = $('section.search-participant-metadata');
		// hide old search params
		$('.search-params-container').remove();
		if (!place.is('#submitted-query')) {
			modifiable = true;
			// show the buttons
			$('button.search-filename, button.search-transcripts, button.search-encounter-metadata, button.search-participant-metadata').show();
		}
		var displayRow = function (row, searchTemplate, rowTemplate, multis) {
			for (var multiIndex = 0, l = multis.length; multiIndex < l; multiIndex ++) {
				multi = multis.eq(multiIndex);
				var fieldMatch = '',
					field = '',
					fieldUnit = '',
					andor = '';
				if (row.find('.field-match-row').length > 0 && row.find('.radio-row').length === 0) {
					fieldMatch = multi.find('.field-match').find(':selected').text();
					field = multi.find('.field').val();
					fieldUnit = multi.find('.field-unit').length ? multi.find('.field-unit').text() : '';
					if (field !== '') {
						var andorSelect = multi.find('.andor-select');
						if ((andorSelect.length > 0) &&
							(multiIndex + 1 < l) &&
							(multis.eq(multiIndex + 1).find('.field').val() !== '')) {
							if (andorSelect.is('select')) andor = andorSelect.val();
							else andor = andorSelect.text();
						}
					}
				} else {
					if (multi.find('.radio-row').length > 0) {
						multi.find(':checked').each(function (selectedIndex, selected) {
							selected = $(selected);
							if (selectedIndex > 0) field += ', ';
							field += $('[for="' + selected.attr('id') + '"]').text();
						});
					} else if (multi.find('select').length > 0) {
						field = multi.find(':selected').text();
					} else {
						field = multi.children().last().val();
					}
				}
				if (field !== '') {
					if (multiIndex === 0) searchTemplate.find('.search-params-list').append(rowTemplate);
					loadFragment('search-shorthand-value-template', function (multiTemplate) {
						rowTemplate.append(multiTemplate);
					}, {fieldMatch:fieldMatch,field:field,fieldUnit:fieldUnit,andor:andor});
				}
			}
		};
		// display new search params by section
		if (filenameSection.length > 0) {
			button = place.find('button.search-filename');
			button.hide();
			loadFragment('search-shorthand-template', function (container) {
				loadFragment('search-shorthand-list-template', function (searchTemplate) {
					filenameSection.find('.form-row').each(function (rowIndex, row) {
						row = $(row);
						var label = row.find('legend, label').first().text(),
							multis = row.find('.multi');
						if (multis.length === 0) multis = row;
						loadFragment('search-shorthand-row-template', function (rowTemplate) {
							displayRow(row, searchTemplate, rowTemplate, multis);
						}, {label:label});
					});
					container.append(searchTemplate);
				}, {separator:''});
				if (modifiable) container.append(button.clone().text('Modify').show());
				if (button.length == 1) {
					button.after(container);
				} else {
					place.append(container);
				}
			}, {heading:button.text()});
		}
		if (transcriptsSection.length > 0) {
			button = place.find('button.search-transcripts');
			button.hide();
			loadFragment('search-shorthand-template', function (container) {
				transcriptsSection.find('.sub').each(function (turnIndex, turn) {
					turn = $(turn);
					var separator = '';
					if (turnIndex > 0) separator = turn.find('.turn-followed-by').find(':selected').text();
					loadFragment('search-shorthand-list-template', function (searchTemplate) {
						turn.find('.form-row').each(function (rowIndex, row) {
							row = $(row);
							var label = row.find('legend, label').first().text(),
								multis = row.find('.multi');
							if (multis.length === 0) multis = row;
							loadFragment('search-shorthand-row-template', function (rowTemplate) {
								displayRow(row, searchTemplate, rowTemplate, multis);
							}, {label:label});
						});
						container.append(searchTemplate);
					}, {separator:separator});
				});
				if (modifiable) container.append(button.clone().text('Modify').show());
				if (button.length == 1) {
					button.after(container);
				} else {
					place.append(container);
				}
			}, {heading:button.text()});
		}
		if (encounterSection.length > 0) {
			button = place.find('button.search-encounter-metadata');
			button.hide();
			loadFragment('search-shorthand-template', function (container) {
				loadFragment('search-shorthand-list-template', function (searchTemplate) {
					encounterSection.find('.form-row').each(function (rowIndex, row) {
						row = $(row);
						var label = row.find('legend, label').first().text(),
							multis = row.find('.multi');
						if (multis.length === 0) multis = row;
						loadFragment('search-shorthand-row-template', function (rowTemplate) {
							displayRow(row, searchTemplate, rowTemplate, multis);
						}, {label:label});
					});
					container.append(searchTemplate);
				}, {separator:''});
				if (modifiable) container.append(button.clone().text('Modify').show());
				if (button.length == 1) {
					button.after(container);
				} else {
					place.append(container);
				}
			}, {heading:button.text()});
		}
		if (participantsSection.length > 0) {
			button = place.find('button.search-participant-metadata');
			button.hide();
			loadFragment('search-shorthand-template', function (container) {
				participantsSection.find('.sub').each(function (turnIndex, turn) {
					turn = $(turn);
					var separator = '';
					if (turnIndex > 0) separator = turn.find('.participant-followed-by').text();
					loadFragment('search-shorthand-list-template', function (searchTemplate) {
						turn.find('.form-row').each(function (rowIndex, row) {
							row = $(row);
							var label = row.find('legend, label').first().text(),
								multis = row.find('.multi');
							if (multis.length === 0) multis = row;
							loadFragment('search-shorthand-row-template', function (rowTemplate) {
								displayRow(row, searchTemplate, rowTemplate, multis);
							}, {label:label});
						});
						container.append(searchTemplate);
					}, {separator:separator});
				});
				if (modifiable) container.append(button.clone().text('Modify').show());
				if (button.length == 1) {
					button.after(container);
				} else {
					place.append(container);
				}
			}, {heading:button.text()});
		}
	}

	function isFormEmpty (form) {
		var vals = form.serializeArray(),
			emptyForm = true;
		for (var i = 0, l = vals.length; i < l; i++) {
			if (vals[i].value !== '') {
				emptyForm = false;
				break;
			}
		}
		return emptyForm;
	}

	function enhanceFields (fragment) {
		if (typeof(fragment) == 'undefined') fragment = $('form:visible');

		// fields with autocomplete from server
		fragment.find('.autocomplete').each(function (index, field) {
			field = $(field);
			field.autocomplete({
				source: function (request, response) {
					$.ajax({
						url: field.attr('data-autocomplete-source'),
						dataType: "json",
						data: { root: request.term },
						success: function(data) {
							// data is returned in format {itemtype:[{id:id,value:value},{...}]}
							// we need to map the array objects to objects with a label and value
							for (var key in data) {
								data = data[key];
								break;
							}
							data = $.map(data, function(item) {
								return {
									label: item.value,
									value: item.value
								};
							});
							response(data);
							if (data.length == 0) {
								loadFragment('new-autocomplete-warning-template', function (warning) {
									field.addClass('warning');
									field.after(warning);
								});
							}						}
					});
				},
				search: function (event, ui) {
					field.removeClass('warning');
					field.closest('.form-row, .radio-row, .multi').find('.warning-message').remove();
				}
			});
		});

		// date selectors
		fragment.find('input[type="date"]').datepicker({maxDate: "+0D" });

		// "Other" subfields
		fragment.find('.radio-row .subfield').each(function (index, field) {
			field = $(field);
			var radio = $(field.parent().find('input[type="radio"]')[0]);
			if (!radio.is(':checked')) {
				field.attr('disabled', true).hide();
			}
			field.closest('.form-row').find('input[type="radio"]').bind('change', function (event) {
				if (radio.is(':checked')) {
					field.removeAttr('disabled').show();
					field.focus();
				} else {
					field.attr('disabled', true).hide();
				}
			});
		});

		// Select menus with data from the server
		fragment.find('.select-fillin').each(function (index, field) {
			field = $(field);
			var source = field.attr('data-fillin-source');
			if (source != '') {
				$.get(source, function (data) {
					// data is returned in format {itemtype:[{id:id,value:value},{...}]}
					// we need to add the values in the array to the select
					for (var key in data) {
						data = data[key];
						break;
					}
					for (var i=0,l=data.length; i<l; i++) {
						var option = $('<option>');
						option.text(data[i].value);
						option.attr('value', data[i].value);
						field.append(option);
					}
				}, 'json');
			}
		});

		// file upload
		fragment.find('input[type="file"]').each(function (index, field) {
			field = $(field);
			var parent = field.closest('.form-row, .multi'),
				fieldName = field.attr('name'),
				isKeyField = field.hasClass('main-file');
			field.uploadify({
				swf: 'static/js/uploadify/uploadify.swf',
				uploader: '/ssim-repository-service-1.0.0/rest/encounterAPI/uploadFile',
				buttonText: 'Select File',
				multi: false,
				onInit: function (instance) {
					// put in a hidden field
					var hidden = $('<input>');
					hidden.attr('type', 'hidden');
					hidden.attr('name', fieldName);
					hidden.attr('value', '');
					if (isKeyField) hidden.addClass('main-file');
					parent.append(hidden);
				},
				onUploadStart: function () {
					// hide the button, just show progress
					parent.find('.uploadify-button').hide();
					parent.find('.upload-status').remove();
					parent.find('input[type="hidden"]').attr('value', '');
				},
				onCancel: function () {
					// show the button again
					parent.find('.uploadify-button').show();
				},
				onUploadSuccess: function (file, data, response) {
					// use file name as base encounter name
					if (isKeyField) baseEncounterName = file.name.substring(0, file.name.lastIndexOf(file.type));
					// show success message where the button was
					loadFragment('upload-success-template', function (msg) {
						showUploadedFile(file.name, msg, parent);
					}, {file:file.name});
					// trigger re-validation of main files
					if (typeof $().validate == 'function') {
						$('.main-file').valid();
					}
				},
				onUploadError: function (file, errorCode, errorMsg, errorString) {
					// show the button again and display an error message
					parent.find('.uploadify-button').show();
					loadFragment('upload-success-template', function (msg) {
						parent.append(msg);
					}, {file:file.name, error:errorString});
				}
			});
		});

		// prevent "enter" from submitting the forms
		fragment.find('input:not([type="submit"])').bind('keydown', function (event) {
			if (event.keyCode == 10 || event.keyCode == 13) 
				event.preventDefault();
		});
	}
	enhanceFields($('#upload'));
	$('body').bind('templateLoaded', function (event) {
		enhanceFields($(event.target));
	});

	// handle editing
	if (editing && $('#upload').length > 0) {
		var form = $('#upload'),
			url = '/ssim-repository-service-1.0.0/rest/encounterAPI/encounter';
		showLoader('body');
		$.ajax(url, {
			type: 'GET',
			dataType: 'json',
			success: function (data) {
				hideLoader('body');
				data = data.artifactsType;
				// fill in the fields
				for (var fieldName in data) {
					if (fieldName == 'encounters' || fieldName == 'related') continue;
					fillField(fieldName, data[fieldName], form);
				}
				// set the other data
				if (typeof data.encounters != 'undefined') {
					encounters = [data.encounters];
					listEncounters($('#encounter-list'));
				}
				if (typeof data.related != 'undefined') {
					// TODO
				}
				// get the base encounter name
				if (encounters.length > 0) {
					var suffixed = encounters[0]['encounter-name'];
					baseEncounterName = suffixed.substring(0, suffixed.lastIndexOf('_'));
				} else {
					var mainFiles = form.find('.main-file');
					for (var i = 0, l = mainFiles.length; i < l; i++) {
						var val = mainFiles.eq(i).val();
						if (val != '') {
							baseEncounterName = val;
							break;
						}
					}
				}
			},
			error: function (data) {
				// TODO: make this better
				hideLoader('body');
				alert('Unable to find encounter.');
				location.href = window.location.pathname;
			},
			data: {
				'name': query.name
			}
		});
	}

	// Related video search
	$('#related-video-search').click(function(event) {
		event.preventDefault();
		loadFragment('video-search-template', function (d) {
			d.dialog();
			/*d.delegate('.video-name', 'click', function (event) {
				var video_id = $(event.target).attr('data-video-id'),
					video_name = $(event.target).text(),
					updateButtonText = function () {
						if ($('#related-video-display').children().length > 0) {
							$('#related-video-search').text('Find another video...');
						} else {
							$('#related-video-search').text('Find a video...');
						}
					};
				if ($.inArray(video_id, related) == -1) {
					related.push(video_id);
					$('#related-videos').val(related.join(','));
					loadFragment('related-video-template', function (new_item) {
						new_item.attr('data-video-id', video_id)
							.find('.video-name').text(video_name);
						new_item.find('.remove').click(function (removeEvent) {
							removeEvent.preventDefault();
							var video_id = new_item.attr('data-video-id');
							new_item.remove();
							updateButtonText();
							for (var i=0, l=related.length; i<l; i++) {
								if (related[i] == video_id) {
									related.splice(i, 1);
									$('#related-videos').val(related.join(','));
									break;
								}
							}
						});
						$('#related-video-display').append(new_item);
						updateButtonText();
					});
				}
				d.dialog('close');
			});*/
			d.submit(function (event) {
				event.preventDefault();
				// TODO: spinner
				$.ajax('/ssim-repository-service-1.0.0/rest/encounterAPI/fileNames', {
					type: 'GET',
					dataType: 'json',
					success: function (data) {
						// TODO: retrieve the list from the data and put it in the box, then handle a second form submission to select things
						console.log(data);
					},
					error: function (data) {},
					data: {
						'name':$(event.target).find('#related-video-name').val(),
						'limit':10
					}
				});
			});
		});
	});

	// Multiple stimulated recall interviews
	$('#add-stimulated-recall-interview').click(function (event) {
		event.preventDefault();
		addMultiField($(event.target).closest('.multi-section'), false);
	});

	// Show the add encounter form
	$('.add-encounter-metadata').click(function (event) {
		event.preventDefault();
		var encounterNum = encounters.length;
		loadFragment('encounter-metadata-template', function (encounterForm) {
			if (typeof encounters[encounterNum] == 'undefined') encounters[encounterNum] = {};
			if (typeof encounters[encounterNum].participants == 'undefined') encounters[encounterNum].participants = [];
			encounterForm.attr('data-new-encounter', 'true');
			$('.page').hide();
			$('#upload').after(encounterForm);
			encounterForm.show();
			window.scrollTo(0,0);
			encounterForm.trigger('templateLoaded');
		}, {num:encounterNum});
	});

	// Edit an already added encounter
	$('.edit-encounter-metadata').live('click', function (event) {
		event.preventDefault();
		var encounterNum = $(event.target).attr('data-encounter-num') - 1,
			encounter = encounters[encounterNum];
		loadFragment('encounter-metadata-template', function (encounterForm) {
			// go through each field in the encounter and put each value into the appropriate fields by name
			for (var key in encounter) {
				if (key != 'participants') {
					fillField(key, encounter[key], encounterForm);
				}
			}
			listParticipants(encounterNum, encounterForm.find('#participant-list'));
			// change submit button text
			encounterForm.find('.save-encounter-metadata').text('Update Encounter');
			encounterForm.attr('data-new-encounter', 'false');
			// show the encounter form
			$('.page').hide();
			$('#upload').after(encounterForm);
			encounterForm.show();
			window.scrollTo(0,0);
			encounterForm.trigger('templateLoaded');
		}, {num:encounterNum});
	});

	// Remove an encounter that had been added
	$('.remove-encounter-metadata').live('click', function (event) {
		event.preventDefault();
		var encounterNum = $(event.target).attr('data-encounter-num') - 1,
			uploadForm = $('#upload'),
			encounterList = uploadForm.find('#encounter-list');
		// confirmation dialog
		$('<div>Are you sure you want to remove this encounter?</div>').dialog({
			resizable: false,
			modal: true,
			buttons: {
				'Yes': function () {
					// remove encounter from encounters[]
					encounters.splice(encounterNum, 1);
					// update the list of encounters and show the upload form
					listEncounters(encounterList);
					$(this).dialog('close');
				},
				'No': function () {
					$(this).dialog('close');
				}
			}
		});
	});

	// Store the data entered in the encounter form and close it
	$('.save-encounter-metadata').live('click', function(event) {
		event.preventDefault();
		var uploadForm = $('#upload'),
			encounterList = uploadForm.find('#encounter-list'),
			encounterForm = $(event.target).closest('.encounter-form'),
			encounterNum = encounterForm.attr('data-encounter-num'),
			participants = encounters[encounterNum].participants,
			valid = true;
		// validate
		if (typeof $().validate == 'function') {
			$.validator.addMethod('numParticipants', function (value, element) {
				// catch NaN with digit test instead
				if (isNaN(parseInt(value, 10))) return true;
				// give an error for too many participants (warn later for too few)
				return (parseInt(value, 10) >= participants.length);
			}, 'You have entered metadata for more than the indicated number of participants.');
			valid = encounterForm.validate(validationRules).form();
		}
		// save data and remove the form
		// TODO: don't save empty multi-fields
		var save = function () {
			// submit autocomplete values so they'll be available for additional encounters
			sendAutocompletes(encounterForm);
			// convert the fields to JSON
			encounters[encounterNum] = storeFields(encounterForm);
			encounters[encounterNum].participants = participants;
			encounterForm.remove();
			// update the list of encounters and show the upload form
			listEncounters(encounterList);
			uploadForm.show();
			window.scrollTo(0,0);
		};
		if (valid) {
			// warn for not enough participants
			var numParticipants = parseInt($('#encounter-num-participants').val(), 10);
			if (!isNaN(numParticipants) &&
				participants.length < numParticipants) {
				loadFragment('not-enough-participants-template', function (msg) {
					msg.dialog({
						resizable: false,
						modal: true,
						buttons: {
							'Save Anyway': function () {
								$(this).dialog('close');
								save();
							},
							'Cancel': function () {
								$(this).dialog('close');
							}
						}
					});
				}, {indicated:numParticipants, actual:participants.length});
			} else {
				save();
			}
		}
	});

	// Close the encounter form without storing the data in it
	$('.cancel-encounter-metadata').live('click', function (event) {
		event.preventDefault();
		var encounterForm = $(event.target).closest('.encounter-form'),
			encounterNum = encounterForm.attr('data-encounter-num'),
			participants = encounters[encounterNum].participants,
			cancel = function () {
				// if it's a new, unsaved encounter, remove empty encounter from list
				if (encounterForm.attr('data-new-encounter') == 'true') {
					encounters.splice(encounterForm.attr('data-encounter-num'), 1);
				}
				// remove encounter form and show upload form
				encounterForm.remove();
				$('#upload').show();
				window.scrollTo(0,0);
			};
		// confirmation message if form is not empty
		// TODO: fix to account for changes only
		if (participants.length > 0 || !isFormEmpty(encounterForm)) {
			$('<div>If you cancel, you will lose the changes to metadata and participants you have made to this encounter. Are you sure?</div>').dialog({
				resizable: false,
				modal: true,
				buttons: {
					'Yes': function () {
						cancel();
						$(this).dialog('close');
					},
					'No': function () {
						$(this).dialog('close');
					}
				}
			});
		} else {
			cancel();
		}
	});

	// Show the add participant form
	$('.add-participant-metadata').live('click', function (event) {
		event.preventDefault();
		var encounterForm = $(event.target).closest('.encounter-form'),
			encounterNum = encounterForm.attr('data-encounter-num');
			participantNum = encounters[encounterNum].participants.length;
		loadFragment('participant-metadata-template', function (participantForm) {
			$('.page').hide();
			encounterForm.after(participantForm);
			participantForm.show();
			window.scrollTo(0,0);
			participantForm.trigger('templateLoaded');
		}, {enc:encounterNum, par:participantNum});
	});

	// Edit an already added participant
	$('.edit-participant-metadata').live('click', function (event) {
		event.preventDefault();
		var button = $(event.target),
			encounterNum = button.attr('data-encounter-num') - 1,
			participantNum = button.attr('data-participant-num') - 1,
			participant = encounters[encounterNum].participants[participantNum],
			encounterForm = button.closest('.encounter-form');
		loadFragment('participant-metadata-template', function (participantForm) {
			// go through each field and put each value into the appropriate fields by name
			for (var key in participant) {
				fillField(key, participant[key], participantForm);
			}
			// change submit button text
			participantForm.find('.save-participant-metadata').text('Update Participant');
			participantForm.attr('data-new-participant', 'false');
			// show the participant form
			$('.page').hide();
			encounterForm.after(participantForm);
			participantForm.show();
			window.scrollTo(0,0);
			participantForm.trigger('templateLoaded');
		}, {enc:encounterNum, par:participantNum});
	});

	// Remove a participant that had been added
	$('.remove-participant-metadata').live('click', function (event) {
		event.preventDefault();
		var button = $(event.target),
			encounterNum = button.attr('data-encounter-num') - 1,
			participantNum = button.attr('data-participant-num') - 1,
			encounterForm = button.closest('.encounter-form'),
			participantList = encounterForm.find('#participant-list'),
			participants = encounters[encounterNum].participants;
		// confirmation dialog
		$('<div>Are you sure you want to remove this participant?</div>').dialog({
			resizable: false,
			modal: true,
			buttons: {
				'Yes': function () {
					// remove encounter from encounters[]
					participants.splice(participantNum, 1);
					// update the list of encounters and show the upload form
					listParticipants(encounterNum, participantList);
					$(this).dialog('close');
				},
				'No': function () {
					$(this).dialog('close');
				}
			}
		});
	});

	// Store the data entered in the participant form and close it
	$('.save-participant-metadata').live('click', function (event) {
		event.preventDefault();
		var participantForm = $(event.target).closest('.participant-form'),
			participantNum = participantForm.attr('data-participant-num'),
			encounterNum = participantForm.attr('data-encounter-num'),
			encounterForm = $('.encounter-form[data-encounter-num="' + encounterNum + '"]'),
			participantList = encounterForm.find('#participant-list'),
			encounter = encounters[encounterNum],
			valid = true;
		// validate
		if (typeof $().validate == 'function') {
			valid = participantForm.validate(validationRules).form();
		}
		// save data and remove the form
		// TODO: don't save empty multi-fields
		if (valid) {
			// submit autocomplete values so they'll be available for additional participants
			sendAutocompletes(participantForm);
			// convert the fields to JSON
			encounter.participants[participantNum] = storeFields(participantForm);
			participantForm.remove();
			// update the list of participants and show the encounter form
			listParticipants(encounterNum, participantList);
			encounterForm.show();
			window.scrollTo(0,0);
		}
	});

	// Close the participant form without storing the data in it
	$('.cancel-participant-metadata').live('click', function (event) {
		event.preventDefault();
		var participantForm = $(event.target).closest('.participant-form'),
			encounterNum = participantForm.attr('data-encounter-num'),
			encounterForm = $('.encounter-form[data-encounter-num="' + encounterNum + '"]'),
			cancel = function () {
				participantForm.remove();
				encounterForm.show();
				window.scrollTo(0,0);
			};
		// confirmation message if form is not empty
		// TODO: fix to account for changes only
		if (!isFormEmpty(participantForm)) {
			$('<div>If you cancel, you will lose the changes you have made to this participant. Are you sure?</div>').dialog({
				resizable: false,
				modal: true,
				buttons: {
					'Yes': function () {
						cancel();
						$(this).dialog('close');
					},
					'No': function () {
						$(this).dialog('close');
					}
				}
			});
		} else {
			cancel();
		}
	});

	// submit the upload forms
	$('#upload').submit(function (event) {
		event.preventDefault();
		var valid = true;
		// check for video
		baseEncounterName = $.trim(baseEncounterName).replace(/\s/g, '_');
		// validate
		if (typeof $().validate == 'function') {
			$.validator.addMethod('main-file', function (value, element) {
				var files = $('.main-file'),
					hasFile = false;
				for (var i=0, l=files.length; i<l; i++) {
					if ($.trim(files.eq(i).val()) !== '') {
						hasFile = true;
						break;
					}
				}
				return hasFile;
			}, 'Please upload at least one source file.');
			$.validator.addMethod('encounter-placeholder', function (value, element) {
				return (encounters.length > 0);
			}, 'You must enter metadata for at least one encounter.');
			valid = $('#upload').validate(validationRules).form();
		}
		if (valid) {
		// set up the JSON
			var globalData = storeFields($(event.target)),
				url = editing ? '/ssim-repository-service-1.0.0/rest/encounterAPI/updateEncounters' : '/ssim-repository-service-1.0.0/rest/encounterAPI/createEncounters';
			for (var i = 0, l = encounters.length; i < l; i++) {
				// TODO: don't replace previous encounter names when editing
				encounters[i]["encounter-name"] = baseEncounterName + '_' + i;
			}
			globalData.encounters = encounters;
			globalData.related = related;
			console.log(globalData);
			console.log(JSON.stringify(globalData));
			// TODO: add spinner
			// submit data to web service
			$.ajax({
				type: 'POST',
				url: url,
				data: JSON.stringify({ "artifactsType": globalData }),
				contentType: 'application/json; charset=utf-8',
				dataType: 'json',
				success: function(data){
					var type = (globalData['data-descriptor'] == 'video') ? 'video' : 'interview';
					location.href = './' + type + '-success';
				},
				error: function(jqXHR, textStatus, errorThrown) {
					// TODO: remove spinner
					// TODO: make this better
					alert('There was a problem submitting the data: ' + errorThrown);
				}
			});
		}
	});

	// Multiple languages
	$('.add-language').live('click', function (event) {
		event.preventDefault();
		var multiSection = $(event.target).closest('.multi-section');
		addMultiField(multiSection);
		multiSection.find('input').last().focus();
	});

	// Search
	$('button.search-filename').live('click', function (event) {
		event.preventDefault();
		var searchPage = $(event.target).closest('.page'),
			newPage = $('section.search-filename');
		if (newPage.length === 0) {
			loadFragment('search-filename-template', function (form) {
				searchPage.hide();
				searchPage.after(form);
				form.show();
				form.trigger('templateLoaded');
			}, {'multinum':0});
		} else {
			searchPage.hide();
			newPage.first().show();
			window.scrollTo(0,0);
		}
	});
	$('button.search-transcripts').live('click', function (event) {
		event.preventDefault();
		var searchPage = $(event.target).closest('.page'),
			newPage = $('section.search-transcripts');
		if (newPage.length === 0) {
			loadFragment('search-transcripts-template', function (form) {
				searchPage.hide();
				searchPage.after(form);
				form.show();
				form.trigger('templateLoaded');
			}, {'multinum':0});
		} else {
			searchPage.hide();
			newPage.first().show();
			window.scrollTo(0,0);
		}
	});
	$('button.search-encounter-metadata').live('click', function (event) {
		event.preventDefault();
		var searchPage = $(event.target).closest('.page'),
			newPage = $('section.search-encounter-metadata');
		if (newPage.length === 0) {
			loadFragment('search-encounter-metadata-template', function (form) {
				searchPage.hide();
				searchPage.after(form);
				form.show();
				form.trigger('templateLoaded');
			}, {'multinum':0});
		} else {
			searchPage.hide();
			newPage.first().show();
			window.scrollTo(0,0);
		}
	});
	$('button.search-participant-metadata').live('click', function (event) {
		event.preventDefault();
		var searchPage = $(event.target).closest('.page'),
			newPage = $('section.search-participant-metadata');
		if (newPage.length === 0) {
			loadFragment('search-participant-metadata-template', function (form) {
				searchPage.hide();
				searchPage.after(form);
				form.show();
				form.trigger('templateLoaded');
			}, {'multinum':0});
		} else {
			searchPage.hide();
			newPage.first().show();
			window.scrollTo(0,0);
		}
	});
	// start searching immediately will all provided fields
	$('#search-form').submit(function (event) {
		event.preventDefault();
		var searchForm = $(event.target);
		// get all the data in JSON format
		$('section.search-filename, section.search-transcripts, section.search-encounter-metadata, section.search-participant-metadata').each(function (index, section) {
			storeSearchConstraints($(section));
		});
		// TODO: add spinner
		$.ajax({
			type: 'POST',
			url: '/ssim-repository-service-1.0.0/rest/encounterAPI/searchEncounters',
			data: JSON.stringify({ "searchType": searchQuery }),
			contentType: 'application/json; charset=utf-8',
			// dataType: 'json',
			success: function(data){
				var result = data.encounterResponse;
				loadFragment('search-results-template', function (template) {
					searchForm.hide();
					searchForm.after(template);
					listSearchConstraints(template.find('#submitted-query'));
					template.find('#modify-search').click(function (event) {
						event.preventDefault();
						template.remove();
						searchForm.find('.page').hide();
						searchForm.find('.search-main').show();
						searchForm.show();
						window.scrollTo(0,0);
						listSearchConstraints(searchForm);
					});
					if (result.length > 0) {
						loadFragment('search-results-list-template', function (list) {
							template.find('#search-results-list').append(list);
							var resultTemplateName = (searchQuery.transcript instanceof Array) ? 'search-single-result-transcript-template' : 'search-single-result-template';
							for (var i = 0, l = result.length; i < l; i++) {
								var more = result[i].hits - 1;
								var hits = "Occurs " + more + " more times";
								if (more == 1) hits = "Occurs " + more + " more time";
								else if (more == 0) hits = "";
								loadFragment(resultTemplateName, function (resultTemplate) {
									list.find('#search-results').append(resultTemplate);
								}, {name:result[i]['encounter-name'], line:result[i].phrase, hits:hits});
							}
						});
					} else {
						loadFragment('no-search-results-template', function (noresults) {
							template.find('#search-results-list').append(noresults);
						});
					}
				});
			},
			error: function(jqXHR, textStatus, errorThrown) {console.log('failure');
				// TODO: remove spinner
				// TODO: make this better
				alert('There was a problem submitting the data: ' + errorThrown);
			}
		});
	});
	$('#search-results-form').live('submit', function (event) {
		event.preventDefault();
		var checkedFiles = $(event.target).find(':checked');
		if (checkedFiles.length > 0) {
			loadFragment('search-download-formats-template', function (d) {
				checkedFiles.each(function (index, checkbox) {
					checkbox = $(checkbox);
					var name = checkbox.attr('name'),
						value = checkbox.val();
					d.append('<input type="hidden" name="' + name + '" value="' + value + '" />');
				});
				d.dialog({width:600});
				d.submit(function (event) {
					event.preventDefault();
					// TODO: make this give warnings about missing files
					$.fileDownload('/ssim-repository-service-1.0.0/rest/encounterAPI/encFileRequest?' + d.serialize(), {
						preparingMessageHtml: "Your download should start in a moment.",
						failMessageHtml: "There was a problem generating your download, please try again."
					});
				});
			});
		} else {
			// TODO: make this better
			alert('Please select at least one of the results to download.');
		}
	});
	// save subpage fields and return to the main page
	$('.add-search-constraints').live('click', function (event) {
		event.preventDefault();
		var searchPage = $('.search-main'),
			currentPage = $(event.target).closest('.page'),
			button;
		// remove the subpage and go back
		currentPage.hide();
		searchPage.show();
		window.scrollTo(0,0);
		// display the saved data on the main page in the corresponding section
		currentPage.removeClass('new-constraint');
		storeSearchConstraints(currentPage);	// submit handler stores everything again, but this helps us cancel changes
		listSearchConstraints(searchPage);
	});
	// Cancel search subpages
	$('.cancel-search-page').live('click', function (event) {
		event.preventDefault();
		var searchPage = $('.search-main'),
			currentPage = $(event.target).closest('.page'),
			newConstraint = currentPage.hasClass('new-constraint');
		// TODO: confirmation message if fields are filled in
		if (newConstraint) {
			currentPage.remove();
		} else {
			if (currentPage.hasClass('search-filename')) {
				for (var fieldName in searchQuery.filename) {
					fillField(fieldName, searchQuery.filename[fieldName], currentPage);
				}
			}
			// add transcript search
			else if (currentPage.hasClass('search-transcripts') || currentPage.hasClass('search-participant-metadata')) {
				// searchQuery.transcript = [];
				var turns = currentPage.find('.sub'),
					visibleTurns = turns.length,
					querySection = (currentPage.hasClass('search-transcripts')) ? searchQuery.transcript : searchQuery.participants;
					storedTurns = querySection.length;
				if (storedTurns < visibleTurns) {
					// take out the extras
					for (var i = visibleTurns - 1; i >= storedTurns; i--) {
						turns.eq(i).remove();
					}
					turns.eq(storedTurns - 1).find('.add-more').show();
					turns = currentPage.find('.sub');
				} else if (storedTurns > visibleTurns) {
					// add more (this shouldn't happen)
					var difference = storedTurns - visibleTurns;
					for (var i = 0; i < difference; i++) {
						turns.last().find('.add-more').click();
					}
					turns = currentPage.find('.sub');
				}
				for (var i = 0; i < storedTurns; i++) {
					for (var fieldName in querySection[i]) {
						fillField(fieldName, querySection[i][fieldName], turns.eq(i));
					}
				}
			}
			// add encounter search
			else if (searchSection.hasClass('search-encounter-metadata')) {
				for (var fieldName in searchQuery.encounter) {
					fillField(fieldName, searchQuery.encounter[fieldName], currentPage);
				}
			}
			// TODO: participant search
			currentPage.hide();
		}
		searchPage.show();
		window.scrollTo(0,0);
	});

	$('.andor button').live('click', function (event) {
		event.preventDefault();
		var button = $(event.target),
			row = button.closest('.multi'),
			andOrVal = 'and';
		if (button.hasClass('or')) andOrVal = 'or';
		addAndOr(row, andOrVal);
	});

	$('.andor-section .remove').live('click', function (event) {
		event.preventDefault();
		var removeButton = $(event.target),
			removeRow = removeButton.closest('.multi');
		removeAndOr(removeRow);
	});
	
	$('.andor-select').live('change', function (event) {
		var select = $(event.target),
			rows = select.closest('.andor-section').find('.multi');
		rows.each(function (index, el) {
			el = $(el);
			if (index == 0) return;
			if (index == (rows.length - 1)) {
				el.find('.andor > *').hide();
				if (select.val() == 'and') el.find('.and').show();
				else if (select.val() == 'or') el.find('.or').show();
			} else {
				var oldAndor = el.find('.andor-select');
				loadFragment(select.val() + '-template', function (newAndor) {
					oldAndor.after(newAndor);
					oldAndor.remove();
				}, {multinum:index});
			}
		})
	});

	$('.add-search-turn').live('click', function (event) {
		event.preventDefault();
		var button = $(event.target),
			turn = button.closest('.sub');
		loadFragment('search-transcripts-template', function (newTurn) {
			newTurn = newTurn.find('.sub');
			loadFragment('search-next-turn-template', function (rows) {
				button.hide();
				newTurn.prepend(rows);
				turn.after(newTurn);
				rows.trigger('templateLoaded');
			});
			newTurn.trigger('templateLoaded');
		}, {'multinum':0});
	});

	$('.add-search-participant').live('click', function (event) {console.log('click');
		event.preventDefault();
		var button = $(event.target),
			turn = button.closest('.sub');
		loadFragment('search-participant-metadata-template', function (newTurn) {
			newTurn = newTurn.find('.sub');
			loadFragment('search-next-participant-template', function (rows) {
				button.hide();
				newTurn.prepend(rows);
				turn.after(newTurn);
				rows.trigger('templateLoaded');
			});
			newTurn.trigger('templateLoaded');
		}, {'multinum':0});
	});

	$('#search-time-specific').live('change', function (event) {
		event.preventDefault();
		var checkbox = $(event.target),
			subfield = checkbox.closest('.radio-row').find('.subfield');
		if (checkbox.is(':checked')) {
			subfield.show();
			subfield.removeAttr('disabled');
		} else {
			subfield.hide();
			subfield.addAttr('disabled');
		}
	});

	$('.select-all').live('change', function (event) {
		var target = $(event.target),
			targetName = target.attr('data-select-all-name'),
			state = target.is(':checked'),
			form = target.closest('form'),
			targetFields = form.find('[name="' + targetName + '"]'),
			selectAlls = form.find('[data-select-all-name="' + targetName + '"]');
		// (un)check all the target fields
		targetFields.attr('checked', state);
		if (state) {
			targetFields.bind('change', function (event) {
				selectAlls.attr('checked', false);
				targetFields.unbind('change');
			});
		}
		// (un)check any other check all boxes for the same fields
		selectAlls.attr('checked', state);
	});
});