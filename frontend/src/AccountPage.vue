<template>
  <div class="account-container">
    <h2>Welcome, {{ user.firstName }} {{ user.lastName }}</h2>

    <div class="reservations-section">
      <h2>My Reservations</h2>
      <button class="btn create-btn" @click="openCreateModal">Create Reservation</button>
      <table class="reservation-table">
        <thead>
          <tr>
            <th>Reservation ID</th>
            <th>Room Name</th>
            <th>Date</th>
            <th>Start Time</th>
            <th>End Time</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
        <tr v-for="(booking, index) in bookings" :key="booking.id">
          <td>{{ booking.id }}</td>
          <td>{{ getRoomName(booking.roomId) }}</td>
          <td>{{ formatDate(booking.startTime) }}</td>
          <td>{{ formatTime(booking.startTime) }}</td>
          <td>{{ formatTime(booking.endTime) }}</td>
          <td>
            <button class="btn modify-btn" @click="editReservation(index)">Modify</button>
            <button class="btn delete-btn" @click="deleteReservation(booking.id)">Delete</button>
          </td>
        </tr>
        <tr v-if="bookings.length === 0">
          <td colspan="6" class="no-data">No reservations available</td>
        </tr>
      </tbody>
      </table>
    </div>

    <!-- Edit Reservation Modal -->
    <div v-if="showEditModal" class="modal">
      <div class="modal-content">
        <h3>Edit Reservation</h3>
        <form @submit.prevent="saveChanges">
          <div class="form-group">
            <label for="editRoom">Room</label>
            <select id="editRoom" v-model="editForm.roomId" required>
              <option v-for="room in rooms" :key="room.id" :value="room.id">
                {{ room.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="editDate">Date</label>
            <input type="date" id="editDate" v-model="editForm.date" required />
          </div>
          <div class="form-group">
            <label for="editStartTime">Start Time</label>
            <input 
              type="text"
              id="editStartTime" 
              :value="editForm.startTime"
              @input="updateEditStartTime($event.target.value)"
              pattern="([01]?[0-9]|2[0-3]):[0-5][0-9]"
              placeholder="HH:mm"
              required 
            />
          </div>
          <div class="form-group">
            <label for="editEndTime">End Time</label>
            <input 
              type="text"
              id="editEndTime" 
              :value="editForm.endTime"
              @input="updateEditEndTime($event.target.value)"
              pattern="([01]?[0-9]|2[0-3]):[0-5][0-9]"
              placeholder="HH:mm"
              required 
            />
          </div>
          <div class="error-message" v-if="timeError">{{ timeError }}</div>
          <div class="modal-actions">
        <button type="submit" class="btn save-btn">Save</button>
        <button type="button" class="btn cancel-btn" @click="closeEditModal">Cancel</button>
      </div>
    </form>
  </div>
</div>

    <!-- Create Reservation Modal -->
    <div v-if="showCreateModal" class="modal">
      <div class="modal-content">
        <h3>Create Reservation</h3>
        <form @submit.prevent="validateAndCreateReservation">
          <div class="form-group">
            <label for="createRoom">Room</label>
            <select id="createRoom" v-model="createForm.roomId" required>
              <option v-for="room in rooms" :key="room.id" :value="room.id">
                {{ room.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="createDate">Date</label>
            <input type="date" id="createDate" v-model="createForm.date" required />
          </div>
          <div class="form-group">
            <label for="createStartTime">Start Time</label>
            <input
              type="text"
              id="createStartTime"
              :value="createForm.startTime"
              @input="updateCreateStartTime($event.target.value)"
              pattern="([01]?[0-9]|2[0-3]):[0-5][0-9]"
              placeholder="HH:mm"
              required
            />
          </div>
          <div class="form-group">
            <label for="createEndTime">End Times</label>
            <input
              type="text"
              id="createEndTime"
              :value="createForm.endTime"
              @input="updateCreateEndTime($event.target.value)"
              pattern="([01]?[0-9]|2[0-3]):[0-5][0-9]"
              placeholder="HH:mm"
              required
            />
          </div>
          <div class="error-message" v-if="timeError">{{ timeError }}</div>
          <div class="modal-actions">
            <button type="submit" class="btn save-btn">Create</button>
            <button type="button" class="btn cancel-btn" @click="closeCreateModal">Cancel</button>
          </div>
        </form>
      </div>
    </div>

    <button class="btn logout-btn" @click="$emit('logout')">Logout</button>
  </div>
</template>

<script>
import api from './api';
import { TimeUtils } from './TimeUtils';

export default {
  props: {
    user: {
      required: true,
    },
  },
  data() {
    return {
      bookings: [],
      rooms: [],
      showEditModal: false,
      showCreateModal: false,
      timeError: '',
      editForm: {
        id: null,
        roomId: '',
        date: '',
        startTime: '',
        endTime: '',
      },
      createForm: {
        roomId: '',
        date: '',
        startTime: '',
        endTime: '',
      },
      currentBookingIndex: null,
    };
  },
  async created() {
    try {
      // Load rooms and bookings when component is created
      const [roomsData, bookingsData] = await Promise.all([
        api.getAllRooms(),
        api.getUserBookings(this.user.id)
      ]);
      this.rooms = roomsData;
      this.bookings = bookingsData;
    } catch (error) {
      alert('Error loading data: ' + error.message);
    }
  },
methods: {
  getRoomName(roomId) {
    const room = this.rooms.find(r => r.id === roomId);
    return room ? room.name : 'Unknown Room';
  },

  formatDate(date) {
    return TimeUtils.formatDate(date);
  },

  formatTime(time) {
    return TimeUtils.formatTime(time);
  },

  validateAndCreateReservation() {
    this.timeError = '';
    
    if (!this.createForm.date) {
        this.timeError = 'Please select a date';
        return;
    }

    const timeValidation = TimeUtils.isValidTimeRange(
        this.createForm.date,
        this.createForm.startTime,
        this.createForm.endTime
    );

    if (!timeValidation.valid) {
        this.timeError = timeValidation.error;
        return;
    }

    this.createReservation();
  },

  closeEditModal() {
    this.showEditModal = false;
    this.timeError = ''; // Add this line
    this.editForm = {
      id: null,
      roomId: '',
      date: '',
      startTime: '',
      endTime: ''
    };
    this.currentBookingIndex = null;
  },

  updateEditStartTime(value) {
    if (this.validateTimeFormat(value)) {
      this.editForm.startTime = value;
    }
  },

  updateEditEndTime(value) {
    if (this.validateTimeFormat(value)) {
      this.editForm.endTime = value;
    }
  },

  updateCreateStartTime(value) {
    if (this.validateTimeFormat(value)) {
      this.createForm.startTime = value;
    }
  },

  updateCreateEndTime(value) {
    if (this.validateTimeFormat(value)) {
      this.createForm.endTime = value;
    }
  },

  validateTimeFormat(value) {
    const timeRegex = /^([01]?[0-9]|2[0-3]):[0-5][0-9]$/;
    return timeRegex.test(value);
  },

  openCreateModal() {
    this.showCreateModal = true;
  },
  
  closeCreateModal() {
    this.showCreateModal = false;
    this.timeError = ''; // Add this line
    this.createForm = {
      roomId: '',
      date: '',
      startTime: '',
      endTime: '',
    };
  },

  async editReservation(index) {
    const booking = this.bookings[index];
    const startDate = new Date(booking.startTime);
    const endDate = new Date(booking.endTime);
    
    const start = TimeUtils.toUTCPlus1(booking.startTime);
    const end = TimeUtils.toUTCPlus1(booking.endTime);
    
    this.editForm = {
      id: booking.id,
      roomId: booking.roomId,
      date: start.date,
      startTime: start.time,
      endTime: end.time
    };
    this.currentBookingIndex = index;
    this.showEditModal = true;
  },

  async saveChanges() {
    this.timeError = '';
    
    try {
        const timeValidation = TimeUtils.isValidTimeRange(
          this.editForm.date,
          this.editForm.startTime,
          this.editForm.endTime
        );

        if (!timeValidation.valid) {
          this.timeError = timeValidation.error;
          return;
        }

        const startDateTime = TimeUtils.toUTC(this.editForm.date, this.editForm.startTime);
        const endDateTime = TimeUtils.toUTC(this.editForm.date, this.editForm.endTime);

        const updatedBooking = await api.updateBooking(this.editForm.id, {
          roomId: this.editForm.roomId,
          startTime: startDateTime,
          endTime: endDateTime,
          userId: this.user.id
        });
        
        this.bookings[this.currentBookingIndex] = updatedBooking;
        this.closeEditModal();
        alert('Reservation updated successfully!');
    } catch (error) {
        alert('Error updating reservation: ' + error.message);
    }
  },

  async createReservation() {
    try {
      const startDateTime = TimeUtils.toUTC(this.createForm.date, this.createForm.startTime);
      const endDateTime = TimeUtils.toUTC(this.createForm.date, this.createForm.endTime);

      const newBooking = await api.createBooking({
        roomId: this.createForm.roomId,
        startTime: startDateTime,
        endTime: endDateTime,
        userId: this.user.id
      });
      
      this.bookings.push(newBooking);
      this.closeCreateModal();
      alert('Reservation created successfully!');
    } catch (error) {
      alert('Error creating reservation: ' + error.message);
    }
  },

  async deleteReservation(bookingId) {
    if (confirm('Are you sure you want to delete this reservation?')) {
      try {
        await api.deleteBooking(bookingId);
        this.bookings = this.bookings.filter(booking => booking.id !== bookingId);
        alert('Reservation deleted successfully!');
      } catch (error) {
        alert('Error deleting reservation: ' + error.message);
      }
    }
  },
}
};
</script>



<style scoped>
/* Styling the page and modal */
.account-container {
  text-align: center;
  margin: 20px;
}

.reservations-section {
  margin-top: 20px;
}

.reservation-table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px auto;
}

.reservation-table th,
.reservation-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

.reservation-table th {
  background-color: #f4f4f4;
  font-weight: bold;
}

.no-data {
  text-align: center;
  color: #888;
}

.btn {
  margin: 0 5px;
  padding: 5px 10px;
  font-size: 14px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.modify-btn {
  background-color: #5a7dcf;
  color: white;
}

.modify-btn:hover {
  background-color: #4a69b2;
}

.delete-btn {
  background-color: #d9534f;
  color: white;
}

.delete-btn:hover {
  background-color: #c9302c;
}

.logout-btn {
  margin-top: 20px;
  background-color: #5a7dcf;
  color: white;
}

.logout-btn:hover {
  background-color: #4a69b2;
}

/* Styling for the green layout container */
/* Green button styled like the Logout button */
.create-btn {
  background-color: #28a745; /* Green background */
  color: white; /* White text */
  padding: 10px 20px; /* Similar padding as Logout */
  font-size: 16px; /* Same font size */
  border: none; /* No border */
  border-radius: 5px; /* Same rounded corners */
  cursor: pointer; /* Pointer cursor */
  transition: background-color 0.3s ease; /* Smooth hover effect */
  margin: 10px 0; /* Consistent margin */
  display: inline-block; /* Inline-block to match button alignment */
}
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 300px;
  text-align: left;
}

.error-message {
  color: #dc3545;
  margin-top: 10px;
  font-size: 14px;
}

.modal-actions {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}

.save-btn {
  background-color: #5cb85c;
  color: white;
}

.save-btn:hover {
  background-color: #4cae4c;
}

.cancel-btn {
  background-color: #d9534f;
  color: white;
}

.cancel-btn:hover {
  background-color: #c9302c;
}

input[type="time"]::-webkit-datetime-edit-ampm-field {
  display: none;
}

input[type="time"] {
  -webkit-appearance: textfield;
  -moz-appearance: textfield;
}

input[type="text"] {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-family: monospace;
}

input[type="text"]:invalid {
  border-color: #dc3545;
}
</style>
