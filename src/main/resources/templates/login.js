document.getElementById('signinForm').addEventListener('submit', async (event) => {
  event.preventDefault();

  const email = document.getElementById('signinEmail').value;
  const password = document.getElementById('signinPassword').value;

  if (!email || !password) {
    showPopup('Please enter both email and password.', 'error');
    return;
  }

  const credentials = { email, password };

  try {
    const response = await fetch('http://localhost:8000/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    });

    if (response.ok) {
      const result = await response.json();
      showPopup('Login successful!', 'success');

      // Store the access token and user ID (if result.data.id exists)
      localStorage.setItem('accessToken', result.data.accessToken);

      if (result.data.id !== undefined && result.data.id !== null) {
        // Save as string; you can convert it later if needed.
        localStorage.setItem('userid', result.data.id.toString());
      } else {
        console.error("User ID not returned by the login endpoint.");
      }

      window.location.href = '/profile.html'; // Redirect after login
    } else {
      const error = await response.json();
      showPopup(`Login failed: ${error.apiError.message || 'No error message returned'}`, 'error');
    }
  } catch (error) {
    showPopup(`Error: ${error.message}`, 'error');
  }
});

function showPopup(message, type) {
  const popup = document.getElementById('popup');
  const popupMessage = document.getElementById('popup-message');
  popupMessage.textContent = message;
  popup.style.display = 'block';

  const closeBtn = document.getElementsByClassName('close')[0];
  closeBtn.onclick = () => { popup.style.display = 'none'; };

  window.onclick = (event) => {
    if (event.target === popup) {
      popup.style.display = 'none';
    }
  };
}
