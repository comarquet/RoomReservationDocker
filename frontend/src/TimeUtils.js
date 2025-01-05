export const TimeUtils = {
    toUTC(date, timeStr) {
      const [hours, minutes] = timeStr.split(':');
      const localDate = new Date(date);
      localDate.setHours(parseInt(hours), parseInt(minutes));
      return localDate.toISOString();
    },
  
    toUTCPlus1(utcDateStr) {
      const date = new Date(utcDateStr);
      date.setHours(date.getHours() + 1);
      return {
        date: date.toISOString().split('T')[0],
        time: this.to24HourFormat(date)
      };
    },
  
    formatTime(isoDateString) {
      if (!isoDateString) return '';
      const date = new Date(isoDateString);
      date.setHours(date.getHours() + 1);
      return this.to24HourFormat(date);
    },
  
    formatDate(isoDateString) {
      if (!isoDateString) return '';
      const date = new Date(isoDateString);
      return date.toLocaleDateString('en-GB', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    },

    isValidTimeRange(date, startTime, endTime) {
        if (!date || !startTime || !endTime) {
            return {
                valid: false,
                error: 'Date, start time, and end time are required'
            };
        }

        const [startHours, startMinutes] = startTime.split(':').map(Number);
        const [endHours, endMinutes] = endTime.split(':').map(Number);

        // Convert to minutes for easier comparison
        const startTotalMinutes = startHours * 60 + startMinutes;
        const endTotalMinutes = endHours * 60 + endMinutes;

        if (endTotalMinutes <= startTotalMinutes) {
            return {
                valid: false,
                error: 'End time must be after start time'
            };
        }

        // Ensure the times are within valid range (00:00-23:59)
        if (startHours > 23 || startMinutes > 59 || endHours > 23 || endMinutes > 59) {
            return {
                valid: false,
                error: 'Invalid time format'
            };
        }

        return {
            valid: true,
            error: null
        };
    },

    // New method for consistent 24-hour format
    to24HourFormat(date) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    }
};