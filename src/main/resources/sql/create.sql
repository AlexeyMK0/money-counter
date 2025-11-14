CREATE TABLE `account` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `account_name` varchar(255)
);

CREATE TABLE `transaction_category` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `category_name` varchar(255)
);

CREATE TABLE `transaction` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `created_at` varchar(255) NOT NULL,
  `sum` integer NOT NULL,
  'account_id' integer NOT NULL,
  'category_id' integer NOT NULL,
  FOREIGN KEY(`account_id`) REFERENCES `account` (`id`),
  FOREIGN KEY(`category_id`) REFERENCES `transaction_category` (`id`)
);
