import { test, expect } from '@playwright/test';

test.beforeEach(async({page}) => {
    await page.goto('http://localhost:5173/');

    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a load command with no other parameters
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.getByRole("button").click();
  })

  test('after I type into the input bar "load", the command is recognized', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a load command with no other parameters
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad");
    await page.getByRole("button").click();

    //Check the history log for a load command message
    const bad_load = "can not recognize"
    await expect(page.getByTestId("Item 1")).toContainText(bad_load);
    await expect(page.getByLabel("Command input")).toBeEmpty();
  })

  test('I submit a load command, but the path is not recognized', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input a load command with a bogus pathname
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad data/not_valid.csv");
    await page.getByRole("button").click();
  
    //Evaluate the response
    const bad_load = "can not recognize"
    await expect(page.getByTestId("Item 1")).toContainText(bad_load);
    //await expect(page.getByLabel('Command input')).toHaveValue(mock_input)
  });

  test('I submit a load command, and the path is recognized', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input a load command with a valid pathname
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad stardata.csv");
    await page.getByRole("button").click();

    await expect(page.getByTestId("Item 1")).toContainText("successfully loaded stardata.csv");

  });