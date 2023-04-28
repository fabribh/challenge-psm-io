drop table if exists operation_type;

create table operation_type (id bigint not null auto_increment, description varchar(50) not null, primary key(id));

insert into operation_type (description) values ('compra a vista');
insert into operation_type (description) values ('compra parcelada');
insert into operation_type (description) values ('saque');
insert into operation_type (description) values ('pagamento');
