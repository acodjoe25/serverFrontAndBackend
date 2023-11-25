import { test, expect } from '@playwright/test';

test.beforeEach(async({page}) => {
    await page.goto('http://localhost:5173/');

  })

/*
 * accessibility (dont need to test screeen reader or zoom)
 * 1. Key commands (Enter, ArrowUp, ArrowDown) recall that you have to press arrowup/down twice to switch directions :/
 */
test('key up works back to back (two calls)', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //register load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.getByRole("button").click();

    // register view
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockView");
    await page.getByRole("button").click();
    
    await page.getByLabel("Command input").click();
    await page.keyboard.press('ArrowUp');
    await expect(page.getByLabel("Command input")).toHaveValue("register mockView");
    await page.keyboard.press('ArrowUp');
    await expect(page.getByLabel("Command input")).toHaveValue("register mockLoad");
})
test('key down works back to back (two calls)', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //register load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.getByRole("button").click();

    // register view
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockView");
    await page.getByRole("button").click();
    
    await page.getByLabel("Command input").click();
    await page.keyboard.press('ArrowUp');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.keyboard.press('ArrowUp');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.keyboard.press('ArrowDown');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.keyboard.press('ArrowDown');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await expect(page.getByLabel("Command input")).toHaveValue("register mockView");
})
test('up up up down keyboard shortcuts back to back', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //register load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.getByRole("button").click();

    // register view
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockView");
    await page.getByRole("button").click();

    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("1");
    await page.getByRole("button").click();

    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("2");
    await page.getByRole("button").click();

    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("3");
    await page.getByRole("button").click();
    
    await page.getByLabel("Command input").click();
    await page.keyboard.press('ArrowUp');
    await page.keyboard.press('ArrowUp');
    await page.keyboard.press('ArrowUp');
    await page.keyboard.press('ArrowDown');
    await page.keyboard.press('ArrowDown');
    await expect(page.getByLabel("Command input")).toHaveValue("2");
})
test('enter standard (short)', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.keyboard.press('Enter');
    await expect(page.getByTestId("Item 0")).toContainText("Successfully registered command mockLoad");
})
test('enter + arrow commands + no clicks + non-registered command', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.keyboard.press('Enter');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.getByLabel("Command input").fill("hello");
    await page.keyboard.press('Enter');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.getByLabel("Command input").fill("register load");
    await page.keyboard.press('Enter');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.keyboard.press('ArrowUp');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.keyboard.press('ArrowUp');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.keyboard.press('Enter');
    await expect(page.getByTestId("Item 1")).toContainText("Command not recognized");
})
test('enter + arrow commands + no clicks + registered command', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.keyboard.press('Enter');
    await page.getByLabel("Command input").fill("register view");
    await page.keyboard.press('Enter');
    await page.getByLabel("Command input").fill("register load");
    await page.keyboard.press('Enter');
    await page.keyboard.press('ArrowUp');
    await page.keyboard.press('ArrowUp');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.keyboard.press('ArrowDown');
    await page.keyboard.press('ArrowDown');
    await expect(page.getByLabel('Command input')).toBeVisible();
    await page.keyboard.press('Enter');
    await expect(page.getByTestId('Item 3')).toBeVisible();

    await expect(page.getByTestId("Item 3")).toContainText("Successfully registered command load");
})
 