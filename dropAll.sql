drop table if exists authorities cascade;
drop table if exists databasechangelog cascade;
drop table if exists databasechangeloglock cascade;
drop table if exists eav_entities cascade;
drop table if exists eav_relationship cascade;
drop table if exists eav_types_attributes cascade;
drop table if exists eav_types_of_entities cascade;
drop table if exists eav_types_of_entities_eav_types_attributes cascade;
drop table if exists eav_values cascade;
drop table if exists eav_entity_attributes cascade;

drop table if exists entity_attributes cascade;
drop table if exists property cascade;


drop table if exists types_attributes cascade;
drop table if exists types_of_entities cascade;
drop table if exists userconnection cascade;
drop table if exists users cascade;
drop table if exists values cascade;
drop table if exists userprofile cascade;

drop sequence if exists hibernate_sequence_eav_types_attributes;
drop sequence if exists hibernate_sequence_eav_types_of_entities;
drop sequence if exists serial;