CREATE TABLE link_click
(
    id              serial PRIMARY KEY,
    multi_link_name varchar(32) NOT NULL references multi_link (name),
    redirected_to   text        NOT NULL,
    time            timestamp   NOT NULL default now()
);
