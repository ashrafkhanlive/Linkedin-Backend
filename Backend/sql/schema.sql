CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(180) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(120) NOT NULL,
  location VARCHAR(120),
  join_date DATE NOT NULL,
  enabled BIT NOT NULL DEFAULT 0,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  INDEX idx_users_email (email),
  INDEX idx_users_name (name)
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role VARCHAR(30) NOT NULL,
  PRIMARY KEY (user_id, role),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS profiles (
  profile_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  headline VARCHAR(180),
  summary TEXT,
  industry VARCHAR(120),
  website VARCHAR(300),
  profile_image_url VARCHAR(500),
  cover_image_url VARCHAR(500),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS experience (
  experience_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  company_name VARCHAR(160) NOT NULL,
  title VARCHAR(160) NOT NULL,
  location VARCHAR(120),
  start_date DATE NOT NULL,
  end_date DATE,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  CONSTRAINT fk_experience_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS education (
  education_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  school_name VARCHAR(180) NOT NULL,
  degree VARCHAR(160) NOT NULL,
  field_of_study VARCHAR(160),
  start_date DATE NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  CONSTRAINT fk_education_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS skills (
  skill_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  skill_name VARCHAR(100) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  UNIQUE KEY idx_skills_user_skill (user_id, skill_name),
  CONSTRAINT fk_skills_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS posts (
  post_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  post_date TIMESTAMP(6) NOT NULL,
  image_url VARCHAR(500),
  video_url VARCHAR(500),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  INDEX idx_posts_post_date (post_date),
  CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS comments (
  comment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  parent_comment_id BIGINT,
  content TEXT NOT NULL,
  comment_date TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
  CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT fk_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES comments(comment_id)
);

CREATE TABLE IF NOT EXISTS likes (
  like_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  UNIQUE KEY idx_likes_post_user (post_id, user_id),
  CONSTRAINT fk_likes_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
  CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS shares (
  share_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  shared_at TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  CONSTRAINT fk_shares_post FOREIGN KEY (post_id) REFERENCES posts(post_id),
  CONSTRAINT fk_shares_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS connections (
  connection_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  connection_user_id BIGINT NOT NULL,
  connection_status VARCHAR(30) NOT NULL,
  requested_at TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  UNIQUE KEY idx_connections_users (user_id, connection_user_id),
  CONSTRAINT fk_connections_user FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT fk_connections_connection_user FOREIGN KEY (connection_user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS `groups` (
  group_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  group_name VARCHAR(180) NOT NULL,
  description TEXT,
  created_date TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  INDEX idx_groups_name (group_name),
  CONSTRAINT fk_groups_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS group_members (
  group_member_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  group_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  joined_at TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  UNIQUE KEY idx_group_members_group_user (group_id, user_id),
  CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES `groups`(group_id),
  CONSTRAINT fk_group_members_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS verification_tokens (
  token_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token VARCHAR(120) NOT NULL UNIQUE,
  type VARCHAR(40) NOT NULL,
  expires_at TIMESTAMP(6) NOT NULL,
  used_at TIMESTAMP(6),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  INDEX idx_tokens_token (token),
  CONSTRAINT fk_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS follows (
  follow_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  follower_user_id BIGINT NOT NULL,
  following_user_id BIGINT NOT NULL,
  followed_at TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  UNIQUE KEY idx_follows_follower_following (follower_user_id, following_user_id),
  CONSTRAINT fk_follows_follower FOREIGN KEY (follower_user_id) REFERENCES users(user_id),
  CONSTRAINT fk_follows_following FOREIGN KEY (following_user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS notifications (
  notification_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  actor_user_id BIGINT,
  type VARCHAR(60) NOT NULL,
  message VARCHAR(500) NOT NULL,
  read_flag BIT NOT NULL DEFAULT 0,
  notified_at TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  created_by VARCHAR(100),
  deleted BIT NOT NULL DEFAULT 0,
  INDEX idx_notifications_user_read (user_id, read_flag),
  CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT fk_notifications_actor FOREIGN KEY (actor_user_id) REFERENCES users(user_id)
);
