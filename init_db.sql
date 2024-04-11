create table if not exists file_meta (
    id bigserial primary key,
    name varchar (255) not null,
    uuid uuid not null unique,
    content_type varchar(100) null,
    content_size bigint not null,
    uploaded_at timestamp not null
);
