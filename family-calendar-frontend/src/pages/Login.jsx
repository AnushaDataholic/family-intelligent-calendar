import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi, saveUser } from '../api/api';

export default function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    try {
      const res = await authApi.post('/login', form);
      saveUser(res.data);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    }
  }

  return (
    <div className="auth-page">
      <section className="auth-copy">
        <h1>Welcome back.</h1>
        <p>Coordinate events, members, budgets, documents, and family reminders from one place.</p>
      </section>

      <form className="auth-card" onSubmit={handleSubmit}>
        <h2>Login</h2>

        {error && <div className="error">{error}</div>}

        <input
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={e => setForm({ ...form, email: e.target.value })}
          required
        />

        <input
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={e => setForm({ ...form, password: e.target.value })}
          required
        />

        <button className="primary-btn">Login</button>

        <p className="switch-link">
          New here? <Link to="/register">Create account</Link>
        </p>
      </form>
    </div>
  );
}
