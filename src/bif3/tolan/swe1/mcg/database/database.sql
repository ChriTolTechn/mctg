-- docker run --name postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_HOST_AUTH_METHOD=trust -e POSTGRES_DB=simpledatastore -d postgres:latest

-- docker exec -it postgres psql -U postgres simpledatastore

-- CREATE DATABASE mctg_db;

-- \connect mctg_db;

CREATE TABLE IF NOT EXISTS mctg_user (
  id SERIAL PRIMARY KEY,
  username TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  token TEXT NOT NULL,
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
  id SERIAL PRIMARY KEY,
  trade_id TEXT UNIQUE NOT NULL,
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
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  damage REAL NOT NULL,
  belongs_to INTEGER NOT NULL,
  FOREIGN KEY (belongs_to) REFERENCES mctg_user(id),
  FOREIGN KEY (belongs_to) REFERENCES mctg_trade_offer(id),
  FOREIGN KEY (belongs_to) REFERENCES mctg_package(id)
);