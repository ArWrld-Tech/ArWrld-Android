# ArWrld Android
Submission for FOAM challenge(s) for the Gitcoin Sustain Web3 hackathon

![App Icon](https://github.com/ArWrld-Tech/ArWrld-Android/blob/master/ic_launcher-web.png?raw=true)

## Integrations
- 0xEarth : Tokenized LAND of Earth https://0xearth.github.io

- FOAM : Provides the tools to enable a crowdsourced map and decentralized location services. https://foam.space/

- BLVD : Tokenized Map Styles https://opensea.io/assets/blvd?query=BLVD&refinementList%5Basset_contract.address%5D%5B0%5D=0xf101430f3c4295958a06b8366e7a097596f2d612

- ERC-721 / NFT digital collectibles view in Augmented Reality 

### Youtube Demo Video
[![Demo Video](https://img.youtube.com/vi/Rgc2mX6d-ek/0.jpg)](https://www.youtube.com/watch?v=Rgc2mX6d-ek)

## Historic Context URls
https://gitcoin.co/issue/ryan-foamspace/Sustain-Web3-hackathon/3/3960

https://gitcoin.co/issue/ryan-foamspace/Sustain-Web3-hackathon/2/3961

## 0xMap : High Level
Named 0xMap, this repo holds the code for a native Android app that leverages 
Augmented Reality (ArCore) and 3D mapping (Mapbox) to create a visual reference tool for 
FOAM POI data (https://foam.space/). 

Debug APK build : https://drive.google.com/open?id=1lcKpvQED1lsdzTQyL5CiSYUxW2fe_2Ft

## Functinality (Mobile-Friendly Map Viewer)
- At first launch, app will request location and camera permissions (THESE ARE REQUIRED!)
- Once accepted, app will load into a base level navigation layer defaulting to opening the AR / map view
- Nav drawer features various deep links into 3rd party Ethereum wallet apps and various FOAM resources 
- Within the AR / map view, app queries the FOAM API to load POIs within 40 km (~25 mi) 
- Each nearby POI is then drawn onto the map and placed in the AR view based on their status (pending, challenged, listed)
- User can click each POI to then load in more details (this uses the '/poi/details// endpoint)
- Multiple items within the POI details view are clickable (phone number opens dialer, website opens url in browser, address queries GMaps, owner opens wallet address on Etherscan)
- If no POIs are found nearby, a pop up is displayed to the user encouraging them to create some

## Functinality (Verify Points Of Interest In Person)
- Location is periodically checked in the background, and on signifcant changes, a query for neabry FOAM POIs is made
- If a user is found near a challenge POI while app is in the background, they are sent a local notification to easily open into the app and see POI details


### Future Ideas
- Improve AR world item size based on distance from user
- Adjust how many items (query distance) are loaded / displayed based on general density 
- Caching layer for offline support
- iOS equivalent app
- Provide deeper web3 integrations
- Ability to import wallet, direct in-app transactions
- Add settings option to enable background notifications when near ANY FOAM POI (not just challenged)
- Add distance settings for background POI notifications 
- Query and add displays for FOAM Signals onto map and within AR View
- Add extra context layer for those visiting a POI (reviews, pictures, etc)


![Home Screen](https://github.com/dhodge229/Sustain-Web3-hackathon-FOAM/blob/master/previews/app_nav.jpg?raw=true)
