/**
 * This function starts the game at the adminview
 * It is called by jsni to get the flexibility
 * to push those changes
 */
function playGame() {
//    getPlayerStats();
    updateRemainingPlayingTime();
    placeNewItems();
    updateBonusGoals();
    updateNodeBatteries();
    aodvProcessRoutingMessages();
    aodvProcessDataPackets();
    setTimeout(playGame(), 500);
}






