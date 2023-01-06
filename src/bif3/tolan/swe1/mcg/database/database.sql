-- docker run --name postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_HOST_AUTH_METHOD=trust -e POSTGRES_DB=simpledatastore -d postgres:latest

-- docker exec -it postgres psql -U postgres simpledatastore

-- CREATE DATABASE mctg_db;

-- \connect mctg_db;

CREATE TABLE mctg_user (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  elo INTEGER NOT NULL,
  coins INTEGER NOT NULL,
  games_played INTEGER NOT NULL
);

CREATE TABLE mctg_deck (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES mctg_user(id)
);

CREATE TABLE mctg_trade_offer (
  id VARCHAR(50) PRIMARY KEY,
  min_damage INTEGER NOT NULL,
  card_type VARCHAR(20),
  card_group VARCHAR(20),
  user_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES mctg_user(id),
  CHECK((card_type IS NOT NULL AND card_group IS NULL) OR (card_type IS NULL AND card_group IS NOT NULL))
);

CREATE TABLE mctg_package (
  id SERIAL PRIMARY KEY
);

CREATE TABLE mctg_card (
  id VARCHAR(50) PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  damage FLOAT NOT NULL,
  mctg_user_id INTEGER,
  mctg_trade_offer_id VARCHAR(50),
  mctg_package_id INTEGER,
  mctg_deck_id INTEGER,
  FOREIGN KEY (mctg_user_id) REFERENCES mctg_user(id),
  FOREIGN KEY (mctg_trade_offer_id) REFERENCES mctg_trade_offer(id),
  FOREIGN KEY (mctg_package_id) REFERENCES mctg_package(id),
  FOREIGN KEY (mctg_deck_id) REFERENCES mctg_deck(id),
  CHECK ((mctg_user_id IS NOT NULL AND mctg_trade_offer_id IS NULL AND mctg_package_id IS NULL AND mctg_deck_id IS NULL) OR 
        (mctg_user_id IS NULL AND mctg_trade_offer_id IS NOT NULL AND mctg_package_id IS NULL AND mctg_deck_id IS NULL) OR 
        (mctg_user_id IS NULL AND mctg_trade_offer_id IS NULL AND mctg_package_id IS NOT NULL AND mctg_deck_id IS NULL) OR 
        (mctg_user_id IS NULL AND mctg_trade_offer_id IS NULL AND mctg_package_id IS NULL AND mctg_deck_id IS NOT NULL) OR
        (mctg_user_id IS NULL AND mctg_trade_offer_id IS NULL AND mctg_package_id IS NULL AND mctg_deck_id IS NULL))
);