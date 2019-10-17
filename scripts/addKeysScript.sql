DELETE
FROM DataEra;
DELETE
FROM DataInfo;
DROP TABLE Account;
DROP TABLE AccountSenders;
CREATE TABLE IF NOT EXISTS AccountSenders
(
    idSender         varchar(255) not null
        primary key,
    senderPrivateKey varchar(255),
    senderPublicKey  varchar(255)
);
CREATE TABLE IF NOT EXISTS Account
(
    id                 INTEGER NOT NULL
        PRIMARY KEY AUTOINCREMENT,
    accountUrl         VARCHAR(255),
    accountRecipient   VARCHAR(255),
    recipientPublicKey VARCHAR(255),
    user               VARCHAR(50),
    password           VARCHAR(50),
    identityName       VARCHAR(20),
    objectName         VARCHAR(255),
    idSender           VARCHAR(255),
    FOREIGN KEY (idSender) REFERENCES AccountSenders (idSender)
);

INSERT INTO AccountSenders (idSender, senderPrivateKey, senderPublicKey)
VALUES ('MyHouse',
        '14ZNfxzTue3t4P618eoaYFrZHD1Jomi9hrJ7Tm6aHDBC913smJmPfbU37uo5JbmNG4UzhgeT1sbDe89zkBNe88k',
        'B9QCMXdDwyD98S2k1GSJMuxJPzvYBJ9Sr5fDPB1zYDV6');
INSERT INTO AccountSenders (idSender, senderPrivateKey, senderPublicKey)
VALUES ('JaEnergy',
        'xjLUDqmRbmvuqmcUWPLWMZdqzh7YkrJBZbae2rDBRTZbv9qKDSihibCmxyxyWDwCBFWgbEQKgiQkeHnv8UUHkRW',
        'GA8XX9cm4W5ctsQLBcW6hu2LSYBSAT7mxSrsVWBDnJhw');
INSERT INTO AccountSenders (idSender, senderPrivateKey, senderPublicKey)
VALUES ('ErachainDS',
        '3tFgehzEnQFiamLXTZoqCHac4wgt1USSLgDyfwoTS7Cx1Fhg4deJ7qWCBb9rx9X4M6oVJGTkA7bvftwY795qfefG',
        '95ZmQZPPYxzziXi65iCzQ8pB85dYED5apWZGaxy8H3kR');

INSERT INTO Account (accountUrl, accountRecipient, recipientPublicKey, user, password, identityName, objectName,
                     idSender)
VALUES ('http://api-admin.testmoydom.ru',
        '7Nv5UaDubU222yynbNQTsUCZUYr1qW6dx9',
        'B9QCMXdDwyD98S2k1GSJMuxJPzvYBJ9Sr5fDPB1zYDV6',
        's.klokov@erachain.org',
        'erachain',
        'problem',
        'myDomService',
        'ErachainDS');

INSERT OR IGNORE INTO Account (accountUrl, accountRecipient, recipientPublicKey, user, password, identityName, objectName,
                               idSender)
VALUES ('https://app.yaenergetik.ru/api?v2',
        '7AjWLhrtBxxsw7zsoqT79FMU6VSY81NCr3',
        'GA8XX9cm4W5ctsQLBcW6hu2LSYBSAT7mxSrsVWBDnJhw',
        's.klokov@erachain.org',
        'erachain',
        'meter',
        'energyService',
        'ErachainDS');
SELECT Account.user as username, password,'1' as enabled FROM Account WHERE user='s.klokov@erachain.org';
SELECT Account.user as username, 'admin_role' as role FROM Account WHERE  user='s.klokov@erachain.org';
SELECT  runDate AS date, blockTrId AS tx, partNo, offset AS pos, lengh AS size
FROM DataInfo a, DataEra b
WHERE identity = ? AND runDate < ? AND dataInfoId = a.id
  AND actRequestId IN (SELECT actRequestId FROM ActParams WHERE paramValue = ? AND paramName = ?)
  AND actRequestId IN (SELECT actRequestId FROM ActParams WHERE paramValue = ? AND paramName = ?)
ORDER BY runDate DESC LIMIT ?;
SELECT actRequestId FROM ActParams WHERE paramValue = ? AND paramName = ?;
SELECT actRequestId FROM ActParams WHERE paramValue = ? AND paramName = ?;
SELECT  runDate AS date, blockTrId AS tx, partNo, offset AS pos, lengh AS size
FROM DataInfo a, DataEra b
WHERE identity = ? AND runDate < ? AND dataInfoId = a.id