-- docker run --name postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_HOST_AUTH_METHOD=trust -e POSTGRES_DB=simpledatastore -d postgres:latest

-- docker exec -it postgres psql -U postgres simpledatastore

-- CREATE DATABASE mctg_db;

-- \connect mctg_db;

CREATE TABLE IF NOT EXISTS mctg_user (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  elo INTEGER NOT NULL,
  coins INTEGER NOT NULL,
  games_played INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS mctg_deck (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES mctg_user(id)
);

CREATE TYPE card_type AS ENUM ('Goblin', 'Dragon', 'Wizard', 'Ork', 'Knight', 'Kraken', 'Elf', 'Spell');
CREATE TYPE card_group AS ENUM ('Monster', 'Spell');

CREATE TABLE IF NOT EXISTS mctg_trade_offer (
  id VARCHAR(50) PRIMARY KEY,
  min_damage INTEGER NOT NULL,
  card_type card_type,
  card_group card_group,
  user_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES mctg_user(id)
);

CREATE TABLE IF NOT EXISTS mctg_package (
  id SERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS mctg_card (
  id VARCHAR(50) PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  damage FLOAT NOT NULL,
  mctg_user_id INTEGER,
  mctg_trade_offer_id VARCHAR(50),
  mctg_package INTEGER,
  FOREIGN KEY (mctg_user_id) REFERENCES mctg_user(id),
  FOREIGN KEY (mctg_trade_offer_id) REFERENCES mctg_trade_offer(id),
  FOREIGN KEY (mctg_package) REFERENCES mctg_package(id),
  CHECK ((mctg_user_id IS NOT NULL AND mctg_trade_offer_id IS NULL AND mctg_package IS NULL) OR 
        (mctg_user_id IS NULL AND mctg_trade_offer_id IS NOT NULL AND mctg_package IS NULL) OR 
        (mctg_user_id IS NULL AND mctg_trade_offer_id IS NULL AND mctg_package IS NOT NULL))
);