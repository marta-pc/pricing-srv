create table prices (
    id bigint primary key,
    brand_id bigint not null,
    start_date timestamp not null,
    end_date timestamp not null,
    price_list integer not null,
    product_id bigint not null,
    priority integer not null,
    price decimal(10,2) not null,
    currency varchar(3) not null,
    last_update timestamp not null,
    last_update_by varchar(50) not null
);