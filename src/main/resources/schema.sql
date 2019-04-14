
create table IF NOT EXISTS Account
(
  id integer not null
    primary key autoincrement,
  accountUrl VarChar(255),
  publicKey   VarChar(255),
  privateKey  VarChar(255),
  recipient VarChar(255),
  user    varchar(50),
  password varchar(50),
  identityName varchar(20)
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
  partNo    int
);


create table IF NOT EXISTS Request
(
  id        integer     not null
  primary key   autoincrement,
  accountId int,
  period  varchar(10),
  lastRun   timestamp
);


create table IF NOT EXISTS Params
(
  id        integer     not null
  primary key   autoincrement,
  requestId int,
  paramName    varchar(25),
  dateType    varchar(10),
  format  varchar(10),
  defValue varchar(50),
  current  boolean
);

create table IF NOT EXISTS ActRequest
(
  id        integer     not null
  primary key   autoincrement,
  requestId int,
  period  varchar(10),
  DateRun   timestamp
);

create table IF NOT EXISTS ActParams
(
  id        integer     not null
  primary key   autoincrement,
  actRequestId    int,
  paramName    varchar(25),
  paramValue    varchar(50)
);





