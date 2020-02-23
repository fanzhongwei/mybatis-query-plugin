create schema teddy;

set search_path to teddy;
commit;

drop table if exists teddy.t_test;

create table teddy.t_test
(
    id          int             not null,
    name        varchar(300)            not null,
    age         int             not null,
constraint pk_test primary key( id )
);

commit;
