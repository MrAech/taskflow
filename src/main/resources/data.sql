-- Seed data for TaskFlow development/testing
-- Passwords below are MD5 hashes of "password123" (intentional — see BUG #42)

INSERT INTO users (username, email, password_hash, role, created_at) VALUES
('alice', 'alice@example.com', '482c811da5d5b4bc6d497ffa98491e38', 'USER', '2026-01-01 09:00:00'),
('bob', 'bob@example.com', '482c811da5d5b4bc6d497ffa98491e38', 'USER', '2026-01-02 10:00:00'),
('carol', 'carol@example.com', '482c811da5d5b4bc6d497ffa98491e38', 'ADMIN', '2026-01-03 11:00:00'),
('dave', 'dave@example.com', '482c811da5d5b4bc6d497ffa98491e38', 'USER', '2026-01-04 12:00:00');

INSERT INTO projects (name, description, owner_id, created_at) VALUES
('Alpha Project', 'First sample project for TaskFlow', 1, '2026-01-05 09:00:00'),
('Beta Initiative', 'Second sample project with multiple tasks', 2, '2026-01-06 10:00:00'),
('Gamma Sprint', 'Short sprint project', 3, '2026-01-07 11:00:00');

INSERT INTO tasks (title, description, status, priority, assigned_user_id, project_id, created_at, due_date) VALUES
('Set up CI pipeline', 'Configure GitHub Actions for automated builds', 'TODO', 'HIGH', 1, 1, '2026-01-08 09:00:00', '2026-03-01 23:59:59'),
('Write unit tests', 'Achieve 80% coverage across all service classes', 'IN_PROGRESS', 'HIGH', 2, 1, '2026-01-09 10:00:00', '2026-04-01 23:59:59'),
('Update API docs', 'Document all REST endpoints in Swagger/OpenAPI', 'TODO', 'MEDIUM', 1, 2, '2026-01-10 11:00:00', '2026-05-01 23:59:59'),
('Fix login bug', 'Users intermittently fail to log in on mobile', 'TODO', 'HIGH', 3, 2, '2026-01-11 12:00:00', '2026-02-15 23:59:59'),
('Refactor dashboard', 'Clean up dashboard component code', 'TODO', 'LOW', 2, 3, '2026-01-12 13:00:00', '2026-06-01 23:59:59'),
('Performance audit', 'Profile DB queries and optimize slow endpoints', 'TODO', 'MEDIUM', null, 1, '2026-01-13 14:00:00', '2026-07-01 23:59:59');

INSERT INTO tags (name, color) VALUES
('backend', '#3b82f6'),
('frontend', '#f59e0b'),
('urgent', '#ef4444'),
('testing', '#10b981'),
('devops', '#8b5cf6');

INSERT INTO comments (content, task_id, author_id, created_at) VALUES
('This needs to run on every PR merge.', 1, 1, '2026-01-09 08:00:00'),
('I will start with the service layer tests.', 2, 2, '2026-01-10 09:00:00'),
('Can we use Swagger UI for this?', 3, 3, '2026-01-11 10:00:00');
