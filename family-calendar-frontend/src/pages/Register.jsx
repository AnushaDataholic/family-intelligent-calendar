import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi, saveUser } from '../api/api';

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    password: '',
    role: 'PARENT'
  });
  const [error, setError] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    try {
      const res = await authApi.post('/register', form);
      saveUser(res.data);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    }
  }

  return (
    <div className="auth-page">
      <section className="auth-copy">
        <h1>Plan family life with less stress.</h1>
        <p>Shared schedules, family members, event budgets, attachments, and smart notifications in one calm workspace.</p>
      </section>

      <form className="auth-card" onSubmit={handleSubmit}>
        <h2>Create account</h2>

        {error && <div className="error">{error}</div>}

        <input
          placeholder="Full name"
          value={form.fullName}
          onChange={e => setForm({ ...form, fullName: e.target.value })}
          required
        />

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

        <select value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
          <option value="PARENT">Parent</option>
          <option value="ADMIN">Admin</option>
          <option value="CHILD">Child</option>
        </select>

        <button className="primary-btn">Register</button>

        <p className="switch-link">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </form>
    </div>
  );
}
