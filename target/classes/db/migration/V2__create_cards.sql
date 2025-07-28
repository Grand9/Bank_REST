CREATE TABLE cards (
  id BIGINT PRIMARY KEY,
  number_encrypted TEXT NOT NULL,
  owner VARCHAR(100),
  expiration_date DATE,
  balance DECIMAL(19,2) DEFAULT 0,
  status VARCHAR(20),
  user_id BIGINT,
  CONSTRAINT fk_cards_user FOREIGN KEY (user_id) REFERENCES users(id)
);
