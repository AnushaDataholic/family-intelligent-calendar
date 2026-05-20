import { useEffect, useState } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import Navbar from '../components/Navbar';
import { calendarApi, getCurrentUser, getFamilyId } from '../api/api';

export default function CalendarPage() {
  const user = getCurrentUser();
  const familyId = getFamilyId() || '1';

  const [events, setEvents] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [form, setForm] = useState(null);
  const [attachment, setAttachment] = useState({
    attachmentType: 'LINK',
    title: '',
    resourceUrl: '',
    stage: 'BEFORE_EVENT'
  });
  const [actualCost, setActualCost] = useState('');
  const [message, setMessage] = useState('');

  async function loadEvents() {
    const res = await calendarApi.get(
      `/${familyId}/events?from=2026-01-01T00:00:00&to=2026-12-31T23:59:59`
    );

    setEvents(res.data.map(event => ({
      id: event.id,
      title: `${event.title} (${event.status})`,
      start: event.startTime,
      end: event.endTime,
      extendedProps: event
    })));
  }

  useEffect(() => {
    loadEvents();
  }, []);

  function handleSelect(info) {
    setMessage('');
    setForm({
      title: '',
      description: '',
      eventType: 'FAMILY',
      visibility: 'FAMILY',
      location: '',
      startTime: info.startStr.slice(0, 19),
      endTime: info.endStr.slice(0, 19),
      estimatedBudget: 0,
      budgetCategory: 'FAMILY',
      attendeeUserIds: String(user.userId)
    });
  }

  function handleEventClick(info) {
    setSelectedEvent(info.event.extendedProps);
    setAttachment({
      attachmentType: 'LINK',
      title: '',
      resourceUrl: '',
      stage: 'AFTER_EVENT'
    });
    setActualCost('');
  }

  async function createEvent(e) {
    e.preventDefault();
    setMessage('');

    try {
      await calendarApi.post(
        `/${familyId}/events`,
        {
          ...form,
          estimatedBudget: Number(form.estimatedBudget),
          attendeeUserIds: form.attendeeUserIds.split(',').map(id => Number(id.trim()))
        },
        {
          headers: { 'X-User-Id': user.userId }
        }
      );

      setForm(null);
      setMessage('Event created successfully.');
      loadEvents();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Could not create event.');
    }
  }

  async function completeEvent() {
    await calendarApi.patch(
      `/${familyId}/events/${selectedEvent.id}/complete`,
      { actualCost: Number(actualCost) },
      { headers: { 'X-User-Id': user.userId } }
    );

    setSelectedEvent(null);
    loadEvents();
  }

  async function addAttachment(e) {
    e.preventDefault();

    await calendarApi.post(
      `/${familyId}/events/${selectedEvent.id}/attachments`,
      attachment,
      { headers: { 'X-User-Id': user.userId } }
    );

    setAttachment({
      attachmentType: 'LINK',
      title: '',
      resourceUrl: '',
      stage: 'AFTER_EVENT'
    });

    setMessage('Attachment saved.');
  }

  return (
    <div className="app-bg calendar-screen">
      <Navbar />

      <main className="calendar-shell">
        <section className="calendar-header">
          <div>
            <h1>Family Calendar</h1>
            <p>Select a time slot to create an event. Click an event to complete it or add attachments.</p>
          </div>
          <span className="family-chip">Family ID: {familyId}</span>
        </section>

        {message && <div className="notice">{message}</div>}

        <section className="calendar-panel">
          <FullCalendar
            plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
            initialView="timeGridWeek"
            selectable
            selectMirror
            events={events}
            select={handleSelect}
            eventClick={handleEventClick}
            height="720px"
            headerToolbar={{
              left: 'prev,next today',
              center: 'title',
              right: 'dayGridMonth,timeGridWeek,timeGridDay'
            }}
          />
        </section>
      </main>

      {form && (
        <div className="modal-backdrop">
          <form className="event-modal" onSubmit={createEvent}>
            <h2>Create Event</h2>

            <input placeholder="Title" value={form.title} onChange={e => setForm({ ...form, title: e.target.value })} required />
            <input placeholder="Location" value={form.location} onChange={e => setForm({ ...form, location: e.target.value })} />
            <textarea placeholder="Description" value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />

            <div className="grid-2">
              <input type="datetime-local" value={form.startTime} onChange={e => setForm({ ...form, startTime: e.target.value })} />
              <input type="datetime-local" value={form.endTime} onChange={e => setForm({ ...form, endTime: e.target.value })} />
            </div>

            <div className="grid-2">
              <select value={form.eventType} onChange={e => setForm({ ...form, eventType: e.target.value })}>
                <option value="FAMILY">Family</option>
                <option value="HEALTH">Health</option>
                <option value="SCHOOL">School</option>
                <option value="WORK">Work</option>
                <option value="TRAVEL">Travel</option>
              </select>

              <select value={form.budgetCategory} onChange={e => setForm({ ...form, budgetCategory: e.target.value })}>
                <option value="FAMILY">Family</option>
                <option value="HEALTH">Health</option>
                <option value="SCHOOL">School</option>
                <option value="FOOD">Food</option>
                <option value="TRAVEL">Travel</option>
              </select>
            </div>

            <div className="grid-2">
              <input type="number" placeholder="Estimated budget" value={form.estimatedBudget} onChange={e => setForm({ ...form, estimatedBudget: e.target.value })} />
              <input placeholder="Attendee IDs, e.g. 2,3" value={form.attendeeUserIds} onChange={e => setForm({ ...form, attendeeUserIds: e.target.value })} />
            </div>

            <div className="actions">
              <button type="button" onClick={() => setForm(null)}>Cancel</button>
              <button className="primary-btn">Create Event</button>
            </div>
          </form>
        </div>
      )}

      {selectedEvent && (
        <div className="modal-backdrop">
          <div className="event-modal">
            <h2>{selectedEvent.title}</h2>
            <p className="muted">{selectedEvent.startTime} to {selectedEvent.endTime}</p>
            <p>Status: <strong>{selectedEvent.status}</strong></p>
            <p>Estimated budget: ${selectedEvent.estimatedBudget ?? 0}</p>

            <div className="grid-2">
              <input type="number" placeholder="Actual cost" value={actualCost} onChange={e => setActualCost(e.target.value)} />
              <button className="primary-btn" onClick={completeEvent}>Mark Complete</button>
            </div>

            <form className="stack" onSubmit={addAttachment}>
              <h3>Add Attachment</h3>

              <select value={attachment.attachmentType} onChange={e => setAttachment({ ...attachment, attachmentType: e.target.value })}>
                <option value="LINK">Link</option>
                <option value="PHOTO">Photo</option>
                <option value="BILL">Bill</option>
                <option value="DOCUMENT">Document</option>
                <option value="NOTE">Note</option>
              </select>

              <input placeholder="Title" value={attachment.title} onChange={e => setAttachment({ ...attachment, title: e.target.value })} required />
              <input placeholder="URL or document link" value={attachment.resourceUrl} onChange={e => setAttachment({ ...attachment, resourceUrl: e.target.value })} required />

              <select value={attachment.stage} onChange={e => setAttachment({ ...attachment, stage: e.target.value })}>
                <option value="BEFORE_EVENT">Before event</option>
                <option value="AFTER_EVENT">After event</option>
              </select>

              <button className="primary-btn">Save Attachment</button>
            </form>

            <div className="actions">
              <button onClick={() => setSelectedEvent(null)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
