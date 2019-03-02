create table IF NOT EXISTS Accounts
(
  PublicKey VarChar(255),
  TimeStamp datetime,
  Creator   varchar(255),
  Id        Integer not null
    primary key autoincrement
);

create table IF NOT EXISTS Blocks
(
  id            integer   not null
    primary key
                    autoincrement,
  startDate       timestamp not null,
  endDate        timestamp not null,
  numberOfTrans int default 0 not null,
  par_block_id  int       not null,
  block         blob      not null,
  createDate    timestamp
);

create table if NOT EXISTS TransactionInfo
(
   ID          INTEGER not null
       primary key autoincrement,

  TypeId      int,
  FieldName   VarChar(255),
  FieldLength int,
  FieldType   varchar(50),
  MaxValue    int,
  OrderId     int
);


create table IF NOT EXISTS TransactionTypes
(
  Name      Varchar(50),
  TypeId    int,
  UNIQUE(Name, TypeId)
);
create table IF NOT EXISTS Valid_Signatures
(
  Signature VarChar(255)
);

