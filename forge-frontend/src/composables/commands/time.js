export const time = (term, args) => {
  let timeZone = undefined;

  if (args && args.length > 0) {
    const location = args[0].replace(/^-/, '').toLowerCase(); // Remove leading dash
    
    // Simple mapping for common cities/zones
    const zoneMap = {
      'paris': 'Europe/Paris',
      'france': 'Europe/Paris',
      'fr': 'Europe/Paris',
      'lille': 'Europe/Paris',
      'london': 'Europe/London',
      'uk': 'Europe/London',
      'berlin': 'Europe/Berlin',
      'rome': 'Europe/Rome',
      'madrid': 'Europe/Madrid',
      'moscow': 'Europe/Moscow',
      'kiev': 'Europe/Kiev',
      
      // Americas
      'newyork': 'America/New_York',
      'ny': 'America/New_York',
      'losangeles': 'America/Los_Angeles',
      'la': 'America/Los_Angeles',
      'chicago': 'America/Chicago',
      'toronto': 'America/Toronto',
      'vancouver': 'America/Vancouver',
      'mexico': 'America/Mexico_City',
      'saopaulo': 'America/Sao_Paulo',
      'buenosaires': 'America/Argentina/Buenos_Aires',
      'santiago': 'America/Santiago',

      // Asia/Pacific
      'tokyo': 'Asia/Tokyo',
      'beijing': 'Asia/Shanghai',
      'shanghai': 'Asia/Shanghai',
      'hongkong': 'Asia/Hong_Kong',
      'singapore': 'Asia/Singapore',
      'seoul': 'Asia/Seoul',
      'bangkok': 'Asia/Bangkok',
      'mumbai': 'Asia/Kolkata',
      'delhi': 'Asia/Kolkata',
      'dubai': 'Asia/Dubai',
      'sydney': 'Australia/Sydney',
      'melbourne': 'Australia/Melbourne',
      'auckland': 'Pacific/Auckland',

      // Africa
      'lome': 'Africa/Lome',
      'cairo': 'Africa/Cairo',
      'johannesburg': 'Africa/Johannesburg',
      'joburg': 'Africa/Johannesburg',
      'nairobi': 'Africa/Nairobi',
      'lagos': 'Africa/Lagos',
      'casablanca': 'Africa/Casablanca',

      // Special
      'utc': 'UTC',
      'gmt': 'UTC',
      'zulu': 'UTC'
    };

    if (zoneMap[location]) {
      timeZone = zoneMap[location];
    } else {
      // Try to use the arg directly if it looks like a valid IANA zone
      try {
        new Intl.DateTimeFormat('en-US', { timeZone: args[0] }).format(new Date());
        timeZone = args[0];
      } catch (e) {
        term.writeln(`Unknown timezone or location: ${args[0]}`);
        term.writeln('Try: -paris, -london, -ny, -tokyo, -utc');
        return;
      }
    }
  }

  try {
    const options = { 
      hour: '2-digit', 
      minute: '2-digit', 
      second: '2-digit', 
      timeZoneName: 'short',
      timeZone 
    };
    const timeString = new Date().toLocaleTimeString('fr-FR', options);
    term.writeln(timeString);
  } catch (e) {
    term.writeln('Error displaying time.');
  }
};
