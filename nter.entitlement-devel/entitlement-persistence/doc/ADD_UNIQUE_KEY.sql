-- Add unique index to early versions of ENTITLEMENT_DB

USE ENTITLEMENT_DB;

ALTER TABLE ENTITLEMENT_POLICY ADD UNIQUE INDEX (subject, realm, resource);

