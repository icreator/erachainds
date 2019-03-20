
create table IF NOT EXISTS Account
(
  id integer not null
    primary key autoincrement,
  accountUrl VarChar(255),
  publicKey VarChar(255),
  privateKey VarChar(255),
  user    varchar(50),
  password varchar(50),
  identityName varchar(20),
  type       varchar(10),
  runDate    timestamp
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
  transcash varchar(64),
  accDate   timestamp,
  blockId   int,
  transId   int
);





