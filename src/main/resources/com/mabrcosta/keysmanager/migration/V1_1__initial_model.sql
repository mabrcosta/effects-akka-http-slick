create table "public"."keys" (
    "uid" UUID NOT NULL PRIMARY KEY,
    "value" VARCHAR NOT NULL,
    "uid_owner_subject" UUID NOT NULL,
    "uid_creator_subject" UUID,
    "uid_last_modifier_subject" UUID,
    "creation_timestamp" TIMESTAMP NOT NULL,
    "update_timestamp" TIMESTAMP NOT NULL);