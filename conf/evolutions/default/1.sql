# --- !Ups

create table "brands" (
  "id" bigint generated by default as identity(start with 10) not null primary key,
  "name" varchar not null,
  "year" int not null
);

insert into "brands" ("id","name", "year") values (1,'Brand 1', 2010);
insert into "brands" ("id","name", "year") values (2,'Brand 2', 2011);
insert into "brands" ("id","name", "year") values (3,'Brand 3', 2012);
insert into "brands" ("id","name", "year") values (4,'Brand 4', 2013);
insert into "brands" ("id","name", "year") values (5,'Brand 5', 2014);

# --- !Downs

drop table "brands" if exists;
