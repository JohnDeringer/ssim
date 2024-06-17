/**
 * National Training and Education Resource (NTER)
 * Copyright (C) 2012  SRI International
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package org.nterlearning.registry.managed;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Performs a redirect on ajax requests when session has timed out.
 *
 * @author bblonski
 */
public class TimeoutListener implements PhaseListener  {

    @Override
	public void afterPhase(PhaseEvent phaseEvent) {
		// do nothing
	}

	@Override
	public void beforePhase(PhaseEvent phaseEvent) {
		// Check for session timeout
		FacesContext facesContexts = phaseEvent.getFacesContext();
		ExternalContext externalContext = facesContexts.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		boolean newSession = (session == null) || (session.isNew());
		boolean postBack = !externalContext.getRequestParameterMap().isEmpty();
		boolean timedOut = postBack && newSession;
		if (timedOut) {
			// redirect on timeout
			try {
				doRedirect(facesContexts);
			} catch (Throwable t) {
				throw new FacesException("Session timed out", t);
			}
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	/**
	 * Does a regular or ajax redirect.
	 * @param facesContext current context
	 * @throws java.io.IOException if cannot reach redirect location
	 */
	public void doRedirect(FacesContext facesContext) throws IOException {
		ExternalContext ec = facesContext.getExternalContext();

		if (ec.isResponseCommitted()) {
			// redirect is not possible
			return;
		}

		if ((facesContext.getPartialViewContext().isAjaxRequest()
				|| facesContext.getPartialViewContext().isPartialRequest())
				&& facesContext.getResponseWriter() == null
				&& facesContext.getRenderKit() == null) {
			ServletResponse response = (ServletResponse) ec.getResponse();
			ServletRequest request = (ServletRequest) ec.getRequest();
			//response.setCharacterEncoding(request.getCharacterEncoding());

			RenderKitFactory factory =
					(RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);

			RenderKit renderKit =
					factory.getRenderKit(facesContext,
							facesContext.getApplication().getViewHandler().calculateRenderKitId(facesContext));

			ResponseWriter responseWriter =
					renderKit.createResponseWriter(response.getWriter(), null, request.getCharacterEncoding());
			responseWriter = new PartialResponseWriter(responseWriter); // needed for primefaces
			facesContext.setResponseWriter(responseWriter);
		}

		ec.redirect(ec.getRequestContextPath());
	}
}
