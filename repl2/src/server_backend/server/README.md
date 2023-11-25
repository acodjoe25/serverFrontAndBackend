# Server-samshuk-davedog21

## Project Details
### Features
Server has two primary functionalities: The ability to read in a
csv file from the local device, view it, and search through it for keywords;
and the ability to query the American Community Survey API
for the percent broadband access in a particular state and county.

### Contributions
samshuk - Design of csv handler state passing, overall idea of the
broadband handler implementation, bug fixing, testing, documentation.

dhan25 - Implementation of record classes, cache, moshi deserialization,
bug fixing, testing, documentation.

Total time spent: About 30 hours

Repo: https://github.com/cs0320-f23/server-samshuk-davedog21/tree/main

## How to use Server
To use Server, run the server class to set up a local server at Port 3232.
You can choose one of four endpoints: load, view, search, and broadband.

In order to navigate through a local csv file, first load the csv using the 
load endpoint and specify a filename like so:
http://localhost:3232/load?filename=census/dol_ri_earnings_disparity.csv

Server remembers which file is currently loaded even across endpoints, so you
can view the file contents with the view endpoint, like so:
http://localhost:3232/view

To search through the loaded file, go to the search endpoint and specify a 
target query, header name (optional), and header given, like so:
http://localhost:3232/search?query=RI&headerGiven=true&headerName=Location
One can also use a column index as a specification like so by inputting information to
an additional query parameter like so:
http://localhost:3232/search?query=RI&headerGiven=false&column=1


Getting a response from the broadband API does not require any prior loading.
The broadband endpoint takes a US state and a county name as parameters, and 
spaces can be inputted as underscores, like so:
http://localhost:3232/broadband?state=California&county=Los_Angeles_County

# Design Choices
Our implementation of Server follows a general scheme of retrieving parameters
inputted by the user, feeding those parameters into a handler that processes
the inputs, and serializing the results in a JSON format on the server back to 
the user. On the csv side, the server creates three endpoints that each share
information through a custom State class. This was done to allow the server to
remember which file it was looking at even if endpoints were changed. Only files
in the data folder of the project can be access by the server, which was a 
defensive programming decision.

On the broadband side, queries are stored by our server, transformed into usable codes
for the ACS API, sent as a request to the ACS API, and returned to the user as 
in JSON format. We did the processing of the API calls to the
CensusBroadbandDataSource, keeping this section of the functionality separate from the 
actual responses sent by our server in BroadbandHandler. We created a DatasourceException
exception to alert users to situations where the location that they provided isn't in
the table or doesn't exist. 

## Errors and Bugs
During testing, when trying to request multiple endpoints, the endpoint must be called 
by a method in order for the handler to be called. The solution was to add a check in the 
tests for the response code sent by the server on all endpoints. 
Another bug that was resolved was that the broadbandHandler was having internal service errors for every
call. We realized that we were adding the state and county codes as integers and not strings, which was
breaking the request URL.
An existing bug we identified is that searching for a numerical value in a CSV requires that the user put 
quotes around the number in the query parameters for it to work correctly if the number is that target value.
Another existing bug is that the BroadbandPercent handler returns the percentage with a duplicate 
"percent = xx" label back to the user. Lastly, a final bug that we identified was the failiure to try
catch Data Source exceptions from propagating to the server. We ran out of time to identify possible solutions.

An IllegalState error occurred when trying to package using maven in the tests when using the @beforeAll 
methods.

## Tests
edu.brown.cs.student.main.server.TestLoadCSV: 

    TestBadFileInput - Invalid path given to loadcsv

    TestGoodFileInput - Proper execution of loadcsv

    TestErrorDatasource - Empty field for filename

    TestLoadTwice - Makes sure that loading a file after another file works

edu.brown.cs.student.main.server.TestViewCSV: 

    TestGoodResponse - Proper execution of viewcsv

    TestNoFileLoaded - No file loaded before viewcsv endpoint

    TestChangeFileLoaded - A file is loaded in and viewed, then another file is loaded in and viewed.

edu.brown.cs.student.main.server.TestSearchCSV:
    
    TestNoFileLoadedYet - No file loaded on loadcsv endpoint

    TestNoColumnSpecified - Parameter headerGiven is missing

    TestSearchAllColumns - No parameters given for headerGiven or headerName 

    TestNoHeaderNameSpecified - Parameter headerName is not given but headerGiven is provided.

TestBroadbandHandler
    
    TestBroadbandRequestSuccess - Correct functionality and querying to the ACS API

TestBroadbandHandler

    TestBroadbandRequestSuccess - Test a successful connection to the API and then assess certain fundamental properties from obtained data

TestBroadbandDataSource

    TestResolveStateCode - Test successful resolveStateCode method
    
    TestResolveCountyCode - Test successful resolveCountyCode method

    TestGetBroadbandPercent - Test succesful getBroadbandPercent method
