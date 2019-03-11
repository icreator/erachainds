create table IF NOT EXISTS Account
(
  id integer not null
    primary key autoincrement,
  accountUrl VarChar(255),
  publicKey VarChar(255),
  privateKey VarChar(255),
  user    varchar(50),
  passWord varchar(50),
  createDate datetime
);

create table IF NOT EXISTS DataInfo
(
  id  integer   not null
    primary key autoincrement,
  accountId       int ,
  runDate        timestamp not null,
  numberOfRecs int default 0 not null,
  records         blob      not null,
  createDate    timestamp
);

create table if NOT EXISTS RequestInfo
(
   id          INTEGER not null
       primary key autoincrement,
  accountId   int,
  period      varchar(10) ,
  fieldName   VarChar(50),
  OrderId     int
);



