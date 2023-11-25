import { test, expect } from '@playwright/test';

test.beforeEach(async ({page}) => {
    await page.goto('http://localhost:5173/');

    await expect(page.getByLabel('Command input')).toBeVisible();
    //Register mode command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mode");
    await page.getByRole("button").click();
  })

  test('after I type into the input bar "mode", the command is recognized', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input an empty mode command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mode");
    await page.getByRole("button").click();

    //Check the history log for a load command message
    await expect(page.getByTestId("Item 1")).toContainText("Mode does not exist");
    await expect(page.getByLabel("Command input")).toBeEmpty();
  })

  test('I submit a mode command to switch to brief mode', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input a mode brief command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mode brief");
    await page.getByRole("button").click();
  
    //Evaluate the response
    await expect(page.getByTestId("Item 1")).toContainText("mode set to brief");
    //This should fail
    await expect(page.getByTestId("Item 1")).not.toContainText("Command:");

    //Input another command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("test");
    await page.getByRole("button").click();

    //Both of these should fail
    await expect(page.getByTestId("Item 1")).not.toContainText("Command:");
    await expect(page.getByTestId("Item 1")).not.toContainText("Output:");

  });

  test('I submit a mode command to switch to verbose mode', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input a mode verbose command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mode verbose");
    await page.getByRole("button").click();

    await expect(page.getByTestId("Item 1")).toContainText("mode set to verbose");
    // await expect(page.getByLabel("Item 1")).toContainText("Command:");
    // await expect(page.getByLabel("Item 1")).toContainText("Output:");
  });

  test('I change the mode to verbose, input a command, change to brief, and input a command', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Input the mode verbose command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mode verbose");
    await page.getByRole("button").click();

    await expect(page.getByTestId("Item 1")).toContainText("mode set to verbose");

    //Input a random test command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("test");
    await page.getByRole("button").click();

    await expect(page.getByTestId("Item 2")).toContainText("Command:");
    await expect(page.getByTestId("Item 2")).toContainText("Output:");

    //Input the mode brief command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mode brief");
    await page.getByRole("button").click();
  
    //Evaluate the response
    await expect(page.getByTestId("Item 5")).toContainText("mode set to brief");


    //Input another command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("test");
    await page.getByRole("button").click();

    //Both of these should fail
    await expect(page.getByTestId("Item 6")).not.toContainText("Command:");
    await expect(page.getByTestId("Item 6")).not.toContainText("Output:");
  });