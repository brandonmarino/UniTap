# UniTap
A simple NFC app.  
Authors 
Brandon Marino, Daniel Abdalla, Osama Buhammad, Mustafa Alshakhs

Current Version
1.00

The Purpose of this project is to build a virrtual wallet which contains many virtual NFC tags.  These tags will work in tandem with a terminal allowing the user access to buildings and the ability to make payments and spend loyalty points.

Done

1. Simple NDEF messaging between two devices
2. Simple Wallet to XML exportation
3. Implement encryption on a string
4. Navigation Pane and added simple Material Design style navigator and coloured statusbar/actionbar 
5. Simple Card View, need to add functionality
6. The Arduino can fully read cards
7. The arduino can 'sense' the phone.  It knows it's there, its just not passing message. YET.

To Do (In no particular order)

1. Simple messaging between the phone and Arduino using HCE
    -working on it
2. Cards list view 
    -simple version is done, we're building on it
3. Make it look pretty 
    -ongoing struggle, looking into Day/Night themes
4. Make sure android ignores the tag when one phone is pressed against another 
    -We know how to do this actually.  Going to focus on phone -> terminal first
5. Simple Server-Client
6. Keep payload encrypted until it is sent to the terminal.  The key to decrypt is stored on the server and a secret to the server alone.
7. Have the app 'subscribe' to a new card using a username and id
8. Oral Presentation
9. Database Server implementation w/ multiple server architecture
10. Demo it
11. Make the report

Optional Additions

1.  Have the terminal be able to sign into their service
2.  Extend the arduino's functionality to another android application which will be built specifically for the Vendor (kinda like square).  This will use the vendor's device for a data connection.  This is useful in the case where the Vendor is on the road (working at the byward market for example).  And do not have the ability to have a permanent unitap terminal sitting on their desk at all times.
