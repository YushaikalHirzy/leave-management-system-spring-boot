-- Create admin user
INSERT INTO users (username, email, password, role)
VALUES
    ('admin', 'admin@example.com', '$2a$10$yfU4GJ0MFx6RHvgjN4hG5.Y/zUJOVzw6L5ZH1RHqssOXdm1ZLpzHK', 'ROLE_ADMIN'),
    ('manager1', 'manager1@example.com', '$2a$10$yfU4GJ0MFx6RHvgjN4hG5.Y/zUJOVzw6L5ZH1RHqssOXdm1ZLpzHK', 'ROLE_MANAGER'),
    ('employee1', 'employee1@example.com', '$2a$10$yfU4GJ0MFx6RHvgjN4hG5.Y/zUJOVzw6L5ZH1RHqssOXdm1ZLpzHK', 'ROLE_EMPLOYEE')
    ON CONFLICT (username) DO NOTHING;

-- Create departments
INSERT INTO departments (name, description)
VALUES
    ('IT', 'Information Technology Department'),
    ('HR', 'Human Resources Department'),
    ('Finance', 'Finance Department')
    ON CONFLICT (name) DO NOTHING;

-- Create employees
INSERT INTO employees (first_name, last_name, email, phone, hire_date, department_id, user_id)
VALUES
    ('Admin', 'User', 'admin@example.com', '1234567890', '2020-01-01',
     (SELECT id FROM departments WHERE name = 'HR'),
     (SELECT id FROM users WHERE username = 'admin'))
    ON CONFLICT DO NOTHING;

INSERT INTO employees (first_name, last_name, email, phone, hire_date, department_id, user_id)
VALUES
    ('Manager', 'One', 'manager1@example.com', '2345678901', '2020-02-01',
     (SELECT id FROM departments WHERE name = 'IT'),
     (SELECT id FROM users WHERE username = 'manager1'))
    ON CONFLICT DO NOTHING;