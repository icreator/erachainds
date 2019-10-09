
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

create table IF NOT EXISTS DataInfo
(
  id        integer     not null
    primary key   autoincrement,
  accountId int,
  runDate   timestamp   not null,
  data      blob        not null,
  identity  varchar(50) not null,
  subDate   timestamp,
  accDate   timestamp,
  actRequestId   int,
  sendToClientDate timestamp,
  acceptClientDate timestamp
);

create table IF NOT EXISTS DataEra
(
  id        integer     not null
  primary key   autoincrement,
  dataInfoId int,
  signature varchar(64),
  blockTrId   varchar(20),
  partNo    int,
  offset     int default 0,
  lengh      int default 0
);


create table IF NOT EXISTS Request
(
   id           integer not null
        primary key autoincrement,
    accountId    int,
    runPeriod    varchar(10),
    lastRun      timestamp,
    submitPeriod varchar(10) default month not null,
    offUnit      varchar(10),
    offValue     int         default 0
);


create table IF NOT EXISTS Params
(
    id        integer not null
        primary key autoincrement,
    requestId int,
    paramName varchar(25),
    dataType  varchar(10),
    format    varchar(30),
    defValue  varchar(50),
    curValue  int default 0
);

create table IF NOT EXISTS ActRequest
(
  id        integer     not null
  primary key   autoincrement,
  requestId int,
  period  varchar(10),
  DateRun   timestamp,
  DateSubmit timestamp
);

create table IF NOT EXISTS ActParams
(
  id        integer     not null
  primary key   autoincrement,
  actRequestId    int,
  paramName    varchar(25),
  paramValue    varchar(50)
);





