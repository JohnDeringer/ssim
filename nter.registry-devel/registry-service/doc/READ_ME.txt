Adding a new service or binding type to the NTER Registry
1) Modify "domain-objects_x.x.x.xsd" located under nter-registry/src/resources
2) Within the simpleType enumeration "serviceTypeEnum" or "bindingTypeEnum" add
the new value to the end of the list.
3) Clean & build the project.
4) Upon deployment the new enumeration value will be inserted into the database.

