INSERT IGNORE INTO users (user_id, email, password, name, location, join_date, enabled)
VALUES
  (1, 'admin@linkedinclone.local', '$2a$12$admin-password-hash-replace-before-use', 'Platform Admin', 'Bengaluru, India', CURRENT_DATE, 1),
  (2, 'mira@linkedinclone.local', '$2a$12$user-password-hash-replace-before-use', 'Mira Sharma', 'Mumbai, India', CURRENT_DATE, 1);

INSERT IGNORE INTO user_roles (user_id, role)
VALUES (1, 'ADMIN'), (1, 'USER'), (2, 'USER');

INSERT IGNORE INTO profiles (profile_id, user_id, headline, summary, industry, website)
VALUES
  (1, 1, 'Administrator', 'Maintains the LinkedIn Clone backend.', 'Software', 'https://linkedinclone.local'),
  (2, 2, 'Senior Backend Engineer', 'Builds scalable Java services.', 'Technology', 'https://example.com/mira');

INSERT IGNORE INTO skills (skill_id, user_id, skill_name)
VALUES (1, 2, 'Java'), (2, 2, 'Spring Boot'), (3, 2, 'Microservices');

INSERT IGNORE INTO posts (post_id, user_id, content, post_date)
VALUES (1, 2, 'Excited to share a new backend architecture sample built with Spring Boot 3.', CURRENT_TIMESTAMP(6));

INSERT IGNORE INTO `groups` (group_id, user_id, group_name, description, created_date)
VALUES (1, 2, 'Java Full Stack Developers', 'A group for Java, Spring, and cloud engineers.', CURRENT_TIMESTAMP(6));

INSERT IGNORE INTO group_members (group_member_id, group_id, user_id, joined_at)
VALUES (1, 1, 2, CURRENT_TIMESTAMP(6));
