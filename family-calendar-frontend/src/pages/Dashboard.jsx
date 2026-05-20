import { useEffect, useState } from 'react';
import { CalendarDays, Plus, Users } from 'lucide-react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { familyApi, getCurrentUser, saveFamilyId } from '../api/api';

export default function Dashboard() {
  const user = getCurrentUser();
  const [families, setFamilies] = useState([]);
  const [members, setMembers] = useState([]);
  const [familyForm, setFamilyForm] = useState({
    familyName: '',
    timezone: 'America/Los_Angeles'
  });
  const [memberForm, setMemberForm] = useState({
    userId: '',
    role: 'PARENT'
  });
  const [selectedFamilyId, setSelectedFamilyId] = useState(localStorage.getItem('familyId') || '');

  async function loadFamilies() {
    const res = await familyApi.get('', {
      headers: { 'X-User-Id': user.userId }
    });

    setFamilies(res.data);

    if (!selectedFamilyId && res.data.length > 0) {
      const id = res.data[0].id;
      setSelectedFamilyId(String(id));
      saveFamilyId(id);
    }
  }

  async function loadMembers(familyId) {
    if (!familyId) return;

    const res = await familyApi.get(`/${familyId}/members`, {
      headers: { 'X-User-Id': user.userId }
    });

    setMembers(res.data);
  }

  useEffect(() => {
    loadFamilies();
  }, []);

  useEffect(() => {
    loadMembers(selectedFamilyId);
  }, [selectedFamilyId]);

  async function createFamily(e) {
    e.preventDefault();

    const res = await familyApi.post('', familyForm, {
      headers: { 'X-User-Id': user.userId }
    });

    saveFamilyId(res.data.id);
    setSelectedFamilyId(String(res.data.id));
    setFamilyForm({ familyName: '', timezone: 'America/Los_Angeles' });
    loadFamilies();
  }

  async function addMember(e) {
    e.preventDefault();

    await familyApi.post(`/${selectedFamilyId}/members`, {
      userId: Number(memberForm.userId),
      role: memberForm.role
    }, {
      headers: { 'X-User-Id': user.userId }
    });

    setMemberForm({ userId: '', role: 'PARENT' });
    loadMembers(selectedFamilyId);
  }

  function selectFamily(id) {
    setSelectedFamilyId(String(id));
    saveFamilyId(id);
  }

  return (
    <div className="app-bg">
      <Navbar />

      <main className="dashboard">
        <section className="hero-band">
          <h1>Your family command center</h1>
          <p>Create family spaces, manage members, and plan meaningful moments together.</p>
          <Link className="primary-link" to="/calendar">
            <CalendarDays size={18} /> Open Calendar
          </Link>
        </section>

        <div className="dashboard-grid">
          <section className="panel">
            <div className="section-title">
              <Plus size={20} />
              <h2>Create Family</h2>
            </div>

            <form className="stack" onSubmit={createFamily}>
              <input
                placeholder="Family name"
                value={familyForm.familyName}
                onChange={e => setFamilyForm({ ...familyForm, familyName: e.target.value })}
                required
              />
              <input
                placeholder="Timezone"
                value={familyForm.timezone}
                onChange={e => setFamilyForm({ ...familyForm, timezone: e.target.value })}
                required
              />
              <button className="primary-btn">Create Family</button>
            </form>
          </section>

          <section className="panel">
            <div className="section-title">
              <Users size={20} />
              <h2>Families</h2>
            </div>

            <div className="list">
              {families.map(family => (
                <button
                  key={family.id}
                  className={String(family.id) === selectedFamilyId ? 'list-item active' : 'list-item'}
                  onClick={() => selectFamily(family.id)}
                >
                  <span>{family.familyName}</span>
                  <small>{family.timezone}</small>
                </button>
              ))}
            </div>
          </section>

          <section className="panel wide">
            <div className="section-title">
              <Users size={20} />
              <h2>Family Members</h2>
            </div>

            <form className="inline-form" onSubmit={addMember}>
              <input
                placeholder="User ID"
                value={memberForm.userId}
                onChange={e => setMemberForm({ ...memberForm, userId: e.target.value })}
                required
              />
              <select value={memberForm.role} onChange={e => setMemberForm({ ...memberForm, role: e.target.value })}>
                <option value="PARENT">Parent</option>
                <option value="CHILD">Child</option>
                <option value="READ_ONLY">Read only</option>
              </select>
              <button className="primary-btn" disabled={!selectedFamilyId}>Add Member</button>
            </form>

            <div className="table">
              <div className="table-row table-head">
                <span>Membership ID</span>
                <span>User ID</span>
                <span>Role</span>
                <span>Status</span>
              </div>
              {members.map(member => (
                <div className="table-row" key={member.id}>
                  <span>{member.id}</span>
                  <span>{member.userId}</span>
                  <span>{member.role}</span>
                  <span>{member.membershipStatus}</span>
                </div>
              ))}
            </div>
          </section>
        </div>
      </main>
    </div>
  );
}
