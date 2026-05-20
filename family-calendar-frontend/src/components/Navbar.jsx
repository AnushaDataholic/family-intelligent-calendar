import { CalendarDays, Home, LogOut } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { clearUser, getCurrentUser } from '../api/api';

export default function Navbar() {
  const navigate = useNavigate();
  const user = getCurrentUser();

  function handleLogout() {
    clearUser();
    navigate('/login');
  }

  return (
    <nav className="navbar">
      <Link className="brand" to="/dashboard">
        <CalendarDays size={22} />
        Family Intelligent Calendar
      </Link>

      <div className="nav-actions">
        <span className="nav-user">{user?.email}</span>
        <Link to="/dashboard"><Home size={18} /> Dashboard</Link>
        <Link to="/calendar"><CalendarDays size={18} /> Calendar</Link>
        <button onClick={handleLogout}><LogOut size={18} /> Logout</button>
      </div>
    </nav>
  );
}
