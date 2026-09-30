CREATE KEYSPACE IF NOT EXISTS seller_service
WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'datacenter1': 1
};

CREATE TABLE IF NOT EXISTS seller_service.seller_by_id (
    seller_id uuid,
    user_id uuid,
    display_name text,
    status text,
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (seller_id)
);

CREATE TABLE IF NOT EXISTS seller_service.listing_submissions_by_seller_and_month (
    seller_id uuid,
    year_month text,
    created_at timestamp,
    submission_id uuid,
    status text,
    updated_at timestamp,
    title text,
    description text,
    property_type text,
    city text,
    district text,
    address_line text,
    price_amount decimal,
    currency text,
    area decimal,
    room_count int,
    PRIMARY KEY ((seller_id, year_month), created_at, submission_id)
) WITH CLUSTERING ORDER BY (created_at DESC, submission_id ASC);
