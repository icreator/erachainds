INSERT OR IGNORE INTO Account (id, accountUrl , publicKey , privateKey, recipient , user, password, identityName) VALUES
(1, 'https://app.yaenergetik.ru/api?v2','addLater', 'addLater','7LghJWXYwq8yJESBri4FbUcoiectMEw393','s.klokov@erachain.org', 'erachain', 'meter');

INSERT OR IGNORE INTO Request (id, accountId,  period )
VALUES (1, 1, 'hour');

INSERT OR IGNORE INTO Params (id, requestId, paramName, dataType, format, defValue )
VALUES (1, 1, 'type', 'char', '', 'month');

INSERT OR IGNORE INTO Params (id, requestId, paramName, dataType, format, defValue )
VALUES (2, 1, 'value', 'Date', 'yyyy-MM', '');

INSERT OR IGNORE INTO ActRequest (id, requestId,  period )
VALUES (1, 1, 'hour');

INSERT OR IGNORE INTO ActParams (id, actRequestId, paramName, paramValue )
VALUES (1, 1, 'type', 'month');

INSERT OR IGNORE INTO ActParams (id, actRequestId, paramName, paramValue )
VALUES (2, 1, 'value',  '2019-02');