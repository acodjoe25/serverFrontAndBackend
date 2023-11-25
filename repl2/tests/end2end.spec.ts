import { test, expect } from '@playwright/test';


test.beforeEach(async({page}) => {
    await page.goto('http://localhost:5173/');

  })

/*
 * End-to-End integration test
 */
test('end2end load one file + search + load a new file + search ', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //register load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register load");
    await page.getByRole("button").click();

    // load ten-star
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("load ten-star.csv");
    await page.getByRole("button").click();
    
    // checking calls were registered + loaded
    await expect(page.getByTestId("Item 0")).toContainText("Successfully registered command load");
    await expect(page.getByTestId("Item 1")).toContainText("Successfully loaded data/ten-star.csv");

    // call search
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register search");
    await page.getByRole("button").click();

    await expect(page.getByTestId("Item 2")).toContainText("Successfully");


    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("search 0");
    await page.getByRole("button").click();


    await expect(page.getByTestId('Table 3 row 0 entry 0')).toBeVisible();
    await expect(page.getByTestId("Table 3 row 0 entry 0")).toContainText("0");
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("load dol_ri_earnings_disparity.csv");
    await page.getByRole("button").click();
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("search White");
    await page.getByRole("button").click();

    await expect(page.getByTestId("Table 5 row 0 entry 4")).toContainText("$1.00");
})

test('end2end load one file + view ', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //register load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register load");
    await page.getByRole("button").click();
    await expect(page.getByTestId('Item 0')).toBeVisible();

    // load ten-star
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("load dol_ri_earnings_disparity.csv");
    await page.getByRole("button").click();


    await expect(page.getByLabel('Command input')).toBeVisible();

    // checking calls were registered + loaded
    await expect(page.getByTestId("Item 0")).toContainText("Successfully registered command load");
    await expect(page.getByTestId("Item 1")).toContainText("Successfully loaded data/dol_ri_earnings_disparity.csv");

    // call search
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register view");
    await page.getByRole("button").click();

    await expect(page.getByTestId('Item 2')).toContainText("Successfully")

    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("view");
    await page.getByRole("button").click();

    await expect(page.getByTestId('Table 3 row 0 entry 0')).toBeVisible();
    await expect(page.getByTestId("Table 3 row 0 entry 0")).toContainText("State");
})

test('end2end broadband', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //register load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register broadband");
    await page.getByRole("button").click();

    // load ten-star
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("broadband California Orange_County");
    await page.getByRole("button").click();
    
    await expect(page.getByTestId('Item 1')).toBeVisible();

    // checking calls were registered + loaded
    await expect(page.getByTestId("Item 1")).toContainText("Location: Orange County");
})


