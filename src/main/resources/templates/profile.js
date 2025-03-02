document.addEventListener("DOMContentLoaded", function() {
  const profileContainer = document.getElementById('profile');
  const loadingMessage = document.getElementById('loading');
  const errorMessage = document.getElementById('error');

  // Retrieve the userId (it comes as a string)
  const userIdStr = localStorage.getItem("userid");

  if (!userIdStr || userIdStr === "undefined") {
    showError("User ID is missing. Please log in again.");
    loadingMessage.style.display = 'none';
    return;
  }

  // Convert the string to a number
  const userId = parseInt(userIdStr, 10);

  // If conversion fails, report error (NaN check)
  if (isNaN(userId)) {
    showError("User ID is invalid. Please log in again.");
    loadingMessage.style.display = 'none';
    return;
  }

  // Function to fetch user profile
  async function fetchProfile() {
    try {
      const response = await fetch(`http://localhost:8000/api/v1/user-profile/${userId}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });

      if (response.ok) {
        const result = await response.json();
        displayProfile(result.data);
      } else {
        const error = await response.json();
        showError(error.apiError.message || "Failed to fetch profile");
      }
    } catch (error) {
      showError(error.message);
    } finally {
      loadingMessage.style.display = 'none';
    }
  }

  // Function to display user profile
  function displayProfile(profile) {
    profileContainer.innerHTML = `
      <div class="profile-image">
        <img src="${profile.profileImage}" alt="${profile.userName}">
      </div>
      <h2>${profile.userName}</h2>
      <p>${profile.bio}</p>
    `;
  }

  // Function to show error message
  function showError(message) {
    errorMessage.textContent = message;
    errorMessage.classList.remove('hidden');
  }

  fetchProfile();  // Fetch and display user profile on page load
});
