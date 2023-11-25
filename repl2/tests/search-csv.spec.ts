import { test, expect } from '@playwright/test';

test.beforeEach(async ({page}) => {
    //Navigate to URL
    await page.goto('http://localhost:5173/');
    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a search command with no other parameters
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockSearch");
    await page.getByRole("button").click();

    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a search command with no other parameters
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.getByRole("button").click();

    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a search command with no other parameters
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockView");
    await page.getByRole("button").click();

  })

  test('after I type into the input bar "search", the command is recognized', async ({ page }) => {

    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a search command with no other parameters
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockSearch");
    await page.getByRole("button").click();

    //Check the history log for a search command message
    await expect(page.getByTestId("Item 3")).toContainText("please provide a search term and optional column identifier for search");
    await expect(page.getByLabel("Command input")).toBeEmpty();
  })

  test('I submit a search command, but a load command has not been submitted before', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input a search command with no load command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockSearch Planet Pluto");
    await page.getByRole("button").click();
  
    //Evaluate the response
    await expect(page.getByTestId("Item 3")).toContainText("csv file not loaded");
  });

  test('I submit a search command with no column specified', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input a load command with a valid pathname
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad stardata.csv");
    await page.getByRole("button").click();

    await expect(page.getByTestId("Item 3")).toContainText("successfully");

    //Input a search command without a column specified
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockSearch Huge");
    await page.getByRole("button").click();

    //Assert that both entries are contained in the output
    await expect(page.getByTestId("Table 4 row 0 entry 0")).toContainText("Saturn");
    await expect(page.getByTestId("Table 4 row 1 entry 0")).toContainText("Jupiter");
    await expect(page.getByTestId("Table 4 row 0")).toHaveCount(1);
  });

  test('I submit a search command with column specified', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input a load command with a valid pathname
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad stardata.csv");
    await page.getByRole("button").click();

    await expect(page.getByTestId("Item 3")).toContainText("successfully");

    //Input a search command with the wrong column specified
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockSearch Size Pluto");
    await page.getByRole("button").click();
    //Assert that no results were found
    await expect(page.getByTestId("Item 4")).toContainText("cannot find search query");

    //Input a search command with the right column specified
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockSearch Planet Pluto");
    await page.getByRole("button").click();

    //Assert that both entries are contained in the output
    await expect(page.getByTestId("Table 5 row 0 entry 0")).toContainText("Pluto");
    await expect(page.getByTestId("Table 5 row 0 entry 1")).toContainText("Tiny");

  });

  test('I submit a search command after submitting a view command', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input a load command with a valid filepath
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad income_by_race_edited.csv");
    await page.getByRole("button").click();

    await expect(page.getByTestId("Item 3")).toContainText("successfully");

    //Input a view command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockView");
    await page.getByLabel("Command input").click();

    //Input a search command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockSearch Year 2000");
    await page.getByRole("button").click();

    //Assert that search works as intended
    await expect(page.getByTestId("Table 4 row 0 entry 2")).toContainText("67000");
    await expect(page.getByTestId("Table 4 row 1 entry 1")).toContainText("2000");
    await expect(page.getByTestId("Table 4 row 2 entry 1")).toContainText("2000");
    await expect(page.getByTestId("Table 4 row 0 entry 0")).toContainText("Asian");
  });

    test('I submit multiple search commands', async ({ page }) => {
        await expect(page.getByLabel('Command input')).toBeVisible();
    
        //Input a load command with a valid filename
        await page.getByLabel("Command input").click();
        await page.getByLabel("Command input").fill("mockLoad postsecondary_education.csv");
        await page.getByRole("button").click();

        //Input a search command
        await page.getByLabel("Command input").click();
        await page.getByLabel("Command input").fill("mockSearch Brown");
        await page.getByRole("button").click();  

        await expect(page.getByTestId("Table 4 row 0 entry 2")).toContainText("Brown");
        await expect(page.getByTestId("Table 4 row 1 entry 1")).toContainText("2000");
        await expect(page.getByTestId("Table 4 row 2 entry 0")).toContainText("Hispanic");

        //Input a different search command
        await page.getByLabel("Command input").click();
        await page.getByLabel("Command input").fill("mockSearch Hispanic");
        await page.getByRole("button").click();  

        await expect(page.getByTestId("Table 5 row 0 entry 0")).toContainText("Hispanic");
        await expect(page.getByTestId("Table 5 row 1 entry 0")).toContainText("Hispanic");    
    });

    test('I submit a search command, but nothing was found', async ({ page }) => {
        await expect(page.getByLabel('Command input')).toBeVisible();

        //Input a load command with a valid pathname
        await page.getByLabel("Command input").click();
        await page.getByLabel("Command input").fill("mockLoad stardata.csv");
        await page.getByRole("button").click();

        await expect(page.getByTestId("Item 3")).toContainText("successfully");

        //Input a search command to an entry that doesn't exist
        await page.getByLabel("Command input").click();
        await page.getByLabel("Command input").fill("mockSearch fakeData");
        await page.getByRole("button").click();

        await expect(page.getByTestId("Item 4")).toContainText("cannot find search query");
    });

    test("I submit a search command using and index as a column identifier", async({page}) => {
        await expect(page.getByLabel('Command input')).toBeVisible();

        //Input a load command with a valid pathname
        await page.getByLabel("Command input").click();
        await page.getByLabel("Command input").fill("mockLoad postsecondary_education.csv");
        await page.getByRole("button").click();

        await expect(page.getByTestId("Item 3")).toContainText("successfully");

        //Input a search command to an entry that doesn't exist
        await page.getByLabel("Command input").click();
        await page.getByLabel("Command input").fill("mockSearch 0 Asian");
        await page.getByRole("button").click();

        await expect(page.getByTestId("Table 4 row 0 entry 0")).toContainText("Asian");
        await expect(page.getByTestId("Table 4 row 0 entry 3")).toContainText("Female");
    })