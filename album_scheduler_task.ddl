DROP TABLE IF EXISTS task_lock;

CREATE TABLE task_lock (
  id INT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  last_change TIMESTAMP NULL DEFAULT NULL,
  state INT NOT NULL DEFAULT 0,
  lock_token INT NOT NULL DEFAULT 0,
  UNIQUE(name) );


INSERT INTO task_lock
  (id,name,last_change,state)
VALUES
  (0,'album',CURRENT_TIME(),0);



