-- Find any duplicates

USE ENTITLEMENT_DB;

SELECT realm, subject, resource, count(*) total
  FROM entitlement_policy
  GROUP BY realm, subject, resource
  HAVING total > 1;
