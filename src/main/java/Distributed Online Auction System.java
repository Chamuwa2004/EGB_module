package com.auction.system;

import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.jms.*;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
class AuctionManagerBean {
    private Map<String, Auction> activeAuctions = new ConcurrentHashMap<>();

    @Lock(LockType.WRITE)
    public void startAuction(String auctionId) {
        if (!activeAuctions.containsKey(auctionId)) {
            Auction auction = new Auction(auctionId);
            auction.setActive(true);
            activeAuctions.put(auctionId, auction);
        } else {
            System.out.println("Auction " + auctionId + " already exists.");
        }
    }

    @Lock(LockType.WRITE)
    public void stopAuction(String auctionId) {
        Auction auction = activeAuctions.get(auctionId);
        if (auction != null) {
            auction.setActive(false);
        }
    }

    @Lock(LockType.READ)
    public Auction getAuctionStatus(String auctionId) {
        return activeAuctions.get(auctionId);
    }

    @Lock(LockType.WRITE)
    public void updateAuctionBid(String auctionId, double bid, String bidder) {
        Auction auction = activeAuctions.get(auctionId);
        if (auction != null && auction.isActive()) {
            auction.setHighestBid(bid);
            auction.setHighestBidder(bidder);
        }
    }
}

@Stateless
class BidManagerBean {
    @Inject
    private JMSContext jmsContext;

    @Resource(mappedName = "java:/jms/queue/BidQueue")
    private Queue bidQueue;

    @EJB
    private AuctionManagerBean auctionManager;

    public void submitBid(String auctionId, String bidderId, double amount) {
        Bid bid = new Bid(auctionId, bidderId, amount);
        if (validateBid(bid)) {
            jmsContext.createProducer().send(bidQueue, bid);
        }
    }

    private boolean validateBid(Bid bid) {
        Auction auction = auctionManager.getAuctionStatus(bid.getAuctionId());
        return auction != null && auction.isActive() && bid.getAmount() > auction.getHighestBid();
    }
}

@Stateful
class UserSessionBean {
    private String userId;

    @EJB
    private BidManagerBean bidManager;

    public void login(String userId) {
        this.userId = userId;
    }

    public void logout() {
        this.userId = null;
    }

    public String getUserDetails() {
        return userId != null ? "User: " + userId : "Not logged in";
    }

    public void placeBid(String auctionId, double amount) {
        if (userId != null) {
            bidManager.submitBid(auctionId, userId, amount);
        }
    }
}

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/BidQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
class BidProcessorMDB implements MessageListener {
    @Inject
    private JMSContext jmsContext;

    @Resource(mappedName = "java:/jms/topic/BidUpdateTopic")
    private Topic bidUpdateTopic;

    @EJB
    private AuctionManagerBean auctionManager;

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objMessage = (ObjectMessage) message;
            Bid bid = (Bid) objMessage.getObject();
            Auction auction = auctionManager.getAuctionStatus(bid.getAuctionId());
            if (auction != null && auction.isActive() && bid.getAmount() > auction.getHighestBid()) {
                auctionManager.updateAuctionBid(bid.getAuctionId(), bid.getAmount(), bid.getBidderId());
                broadcastUpdate(bid);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void broadcastUpdate(Bid bid) {
        String update = "New highest bid on " + bid.getAuctionId() + ": " + bid.getAmount() + " by " + bid.getBidderId();
        jmsContext.createProducer().send(bidUpdateTopic, update);
    }
}

class Auction implements Serializable {
    private String auctionId;
    private double highestBid;
    private String highestBidder;
    private boolean active;

    public Auction(String auctionId) {
        this.auctionId = auctionId;
        this.highestBid = 0.0;
        this.highestBidder = null;
        this.active = false;
    }

    public String getAuctionId() { return auctionId; }
    public double getHighestBid() { return highestBid; }
    public String getHighestBidder() { return highestBidder; }
    public boolean isActive() { return active; }

    public void setHighestBid(double bid) { this.highestBid = bid; }
    public void setHighestBidder(String bidder) { this.highestBidder = bidder; }
    public void setActive(boolean active) { this.active = active; }
}

class Bid implements Serializable {
    private String auctionId;
    private String bidderId;
    private double amount;

    public Bid(String auctionId, String bidderId, double amount) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
    }

    public String getAuctionId() { return auctionId; }
    public String getBidderId() { return bidderId; }
    public double getAmount() { return amount; }
}