INSERT OR IGNORE INTO AccountSenders (idSender, senderPrivateKey, senderPublicKey)
VALUES ('MyHouse',
        '14ZNfxzTue3t4P618eoaYFrZHD1Jomi9hrJ7Tm6aHDBC913smJmPfbU37uo5JbmNG4UzhgeT1sbDe89zkBNe88k',
        'B9QCMXdDwyD98S2k1GSJMuxJPzvYBJ9Sr5fDPB1zYDV6');
INSERT OR IGNORE INTO AccountSenders (idSender, senderPrivateKey, senderPublicKey)
VALUES ('JaEnergy',
        'xjLUDqmRbmvuqmcUWPLWMZdqzh7YkrJBZbae2rDBRTZbv9qKDSihibCmxyxyWDwCBFWgbEQKgiQkeHnv8UUHkRW',
        'GA8XX9cm4W5ctsQLBcW6hu2LSYBSAT7mxSrsVWBDnJhw');
INSERT OR IGNORE INTO AccountSenders (idSender, senderPrivateKey, senderPublicKey)
VALUES ('ErachainDS',
        '3tFgehzEnQFiamLXTZoqCHac4wgt1USSLgDyfwoTS7Cx1Fhg4deJ7qWCBb9rx9X4M6oVJGTkA7bvftwY795qfefG',
        '95ZmQZPPYxzziXi65iCzQ8pB85dYED5apWZGaxy8H3kR');

-- INSERT OR IGNORE INTO Account (accountUrl, accountRecipient, recipientPublicKey, user, password, identityName,
--                                objectName,
--                                idSender)
-- VALUES ('http://api-admin.testmoydom.ru',
--         '7Nv5UaDubU222yynbNQTsUCZUYr1qW6dx9',
--         'B9QCMXdDwyD98S2k1GSJMuxJPzvYBJ9Sr5fDPB1zYDV6',
--         's.klokov@erachain.org',
--         'erachain',
--         'problem',
--         'myDomService',
--         'ErachainDS');
--
-- INSERT OR IGNORE INTO Params (id, requestId, paramName, dataType, format, defValue, curValue)
-- VALUES (1,
--         1,
--         'authToken',
--         'char',
--         null,
--         '9u5bjskxv4pb7uotfk',
--         0);

-- INSERT OR IGNORE INTO Params (id, requestId, paramName, dataType, format, defValue, curValue)
-- VALUES (2,
--         1,
--         'date',
--         'date',
--         'yyyy-MM-dd''T''HH:mm:ssZ',
--         null,
--         1);


INSERT OR IGNORE INTO Account (id, accountUrl, accountRecipient, recipientPublicKey, user, password, identityName,
                               objectName,
                               idSender)
VALUES (1,
        'https://app.yaenergetik.ru/api?v2',
        '7AjWLhrtBxxsw7zsoqT79FMU6VSY81NCr3',
        'GA8XX9cm4W5ctsQLBcW6hu2LSYBSAT7mxSrsVWBDnJhw',
        's.klokov@erachain.org',
        'erachain',
        'meter',
        'energyService',
        'ErachainDS');

INSERT OR IGNORE INTO Params (id, requestId, paramName, dataType, format, defValue, curValue)
VALUES (1,
        1,
        'type',
        'char',
        '',
        'hour',
        0);

INSERT OR IGNORE INTO Params (id, requestId, paramName, dataType, format, defValue, curValue)
VALUES (2,
        1,
        'value',
        'date',
        'yyyy-MM-dd''T''HH:mm:ssZ',
        '',
        1);

INSERT OR IGNORE INTO Params (id, requestId, paramName, dataType, format, defValue, curValue)
VALUES (3,
        1,
        'mode',
        '',
        null,
        'all',
        null);

INSERT OR IGNORE INTO Request(id,
                              accountId,
                              runPeriod,
                              submitPeriod,
                              offUnit,
                              offValue,
                              timezone)
VALUES (1,
        1,
        'minute',
        'minute',
        'minute',
        1,
        '+12:00')