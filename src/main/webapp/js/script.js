document.addEventListener('DOMContentLoaded', () => {
    // Simulated auction data
    const auctionItems = [
        { id: 1, name: 'Vintage Painting', description: 'A beautiful oil painting from the 19th century.', startPrice: 100, currentBid: 150, endTime: new Date(Date.now() + 3600 * 1000 * 1) }, // Ends in 1 hour
        { id: 2, name: 'Antique Watch', description: 'A gold-plated pocket watch, fully functional.', startPrice: 200, currentBid: 220, endTime: new Date(Date.now() + 3600 * 1000 * 2) } // Ends in 2 hours
    ];

    let currentHighestBid = 220; // Example highest bid
    let auctionTimerInterval;

    const auctionItemsSection = document.getElementById('auction-items');
    const highestBidAmountSpan = document.getElementById('highest-bid-amount');
    const auctionTimerSpan = document.getElementById('auction-timer');
    const bidForm = document.getElementById('bid-form');
    const bidAmountInput = document.getElementById('bid-amount');
    const bidStatusDiv = document.getElementById('bid-status');

    function displayAuctionItems() {
        const itemsPlaceholder = auctionItemsSection.querySelector('.item-placeholder');
        if (itemsPlaceholder) {
            auctionItemsSection.removeChild(itemsPlaceholder);
        }

        auctionItems.forEach(item => {
            const itemDiv = document.createElement('div');
            itemDiv.classList.add('auction-item');
            itemDiv.innerHTML = `
                <h3>${item.name}</h3>
                <p>${item.description}</p>
                <p>Starting Price: $${item.startPrice}</p>
                <p>Current Bid: $${item.currentBid}</p>
                <p>Ends In: <span id="timer-${item.id}">${formatTimeLeft(item.endTime)}</span></p>
            `;
            auctionItemsSection.appendChild(itemDiv);
            startItemTimer(item);
        });
    }

    function formatTimeLeft(endTime) {
        const totalSeconds = Math.floor((endTime - Date.now()) / 1000);
        if (totalSeconds <= 0) return "Auction Ended";

        const hours = Math.floor(totalSeconds / 3600);
        const minutes = Math.floor((totalSeconds % 3600) / 60);
        const seconds = totalSeconds % 60;

        return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    }

    function startItemTimer(item) {
        const timerSpan = document.getElementById(`timer-${item.id}`);
        if (!timerSpan) return;

        setInterval(() => {
            timerSpan.textContent = formatTimeLeft(item.endTime);
        }, 1000);
    }

    function updateMainAuctionTimer() {
        // For simplicity, this main timer can reflect the soonest ending auction or a general auction period
        // Here, we'll simulate a general timer or link it to the first item for demo purposes
        if (auctionItems.length > 0) {
            auctionTimerSpan.textContent = formatTimeLeft(auctionItems[0].endTime);
        } else {
            auctionTimerSpan.textContent = "No Active Auctions";
        }
    }


    function updateHighestBidDisplay() {
        highestBidAmountSpan.textContent = `$${currentHighestBid.toFixed(2)}`;
    }

    bidForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const bidAmount = parseFloat(bidAmountInput.value);

        if (isNaN(bidAmount) || bidAmount <= currentHighestBid) {
            displayBidStatus(`Your bid must be higher than the current highest bid of $${currentHighestBid.toFixed(2)}.`, 'error');
            return;
        }

        // Simulate bid placement
        currentHighestBid = bidAmount;
        updateHighestBidDisplay();
        displayBidStatus(`Successfully placed bid of $${bidAmount.toFixed(2)}. You are the highest bidder!`, 'success');
        bidAmountInput.value = ''; // Clear input field

        // In a real system, this would send the bid to the server via JMS/EJB
        // and receive updates for the highest bid.
    });

    function displayBidStatus(message, type) {
        bidStatusDiv.innerHTML = message;
        bidStatusDiv.className = type; // 'success' or 'error'
        setTimeout(() => {
            bidStatusDiv.innerHTML = '';
            bidStatusDiv.className = '';
        }, 5000); // Message disappears after 5 seconds
    }

    // Initial setup
    displayAuctionItems();
    updateHighestBidDisplay();

    // Start the main auction timer (can be improved to track multiple items)
    if (auctionItems.length > 0) {
        if(auctionTimerInterval) clearInterval(auctionTimerInterval);
        auctionTimerInterval = setInterval(updateMainAuctionTimer, 1000);
        updateMainAuctionTimer(); // Initial call
    }

});
