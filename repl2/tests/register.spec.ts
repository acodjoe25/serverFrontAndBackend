//Registering commands, mocking new commands, checking functionality of commands,
// checking commands don't when you don't register them
import { test, expect } from '@playwright/test';


test.beforeEach(async({page}) => {
    await page.goto('http://localhost:5173/');

  })

test('I try to register a command that is not in the list of accepted commands', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a register command for a bogus command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register bogus");
    await page.getByRole("button").click();

    //Check the history log for an error message
    const bad_load = "Command not found in list of possible commands!"
    await expect(page.getByTestId("Item 0")).toContainText(bad_load);
    await expect(page.getByLabel("Command input")).toBeEmpty();
})

test('I try to register a command successfully', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a register command for a bogus command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register load");
    await page.getByRole("button").click();

    //Check the history log for an error message
    const bad_load = "Command not found in list of possible commands!"
    await expect(page.getByTestId("Item 0")).not.toContainText(bad_load);
    await expect(page.getByTestId("Item 0")).toContainText("Successfully registered command load");
    await expect(page.getByLabel("Command input")).toBeEmpty();
})
/**
 * registering commands
 * 1. command not in list -> reutrns an error in history |DONE
 * 2. succesful register of load | DONE
 * 3. registering 2 commands back to back + calling first command | DONE
 * 4. registering same command twice -> still registered |DONE
 */
test('registering 2 command back to back and then calling the first one', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //register load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.getByRole("button").click();

    // register view
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockView");
    await page.getByRole("button").click();
    
    // checking calls were registered
    await expect(page.getByTestId("Item 0")).toContainText("Successfully registered command mockLoad");
    await expect(page.getByTestId("Item 1")).toContainText("Successfully registered command mockView");

    // call load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad stardata.csv"); 
    await page.getByRole("button").click();
    
    // test to see if load was succesful
    await expect(page.getByTestId("Item 2")).toContainText("loaded stardata.csv"); 
    await expect(page.getByLabel("Command input")).toBeEmpty();
})

test('registering the same command twice in a row does not return an error', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //register load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register view");
    await page.getByRole("button").click();
    // register view
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register view");
    await page.getByRole("button").click();
    
    // checking calls were registered
    await expect(page.getByTestId("Item 0")).toContainText("Successfully registered command view");
    await expect(page.getByTestId("Item 1")).toContainText("Successfully registered command view");
    await expect(page.getByLabel("Command input")).toBeEmpty();
})


/*
 *  checking functionality of commands edge cases (use different commands for each test) 
 * 1. regfister a command + call it -> works |DONE
 * 2. dont register a command + call -> doesnt work |DONE
 * 3. dont register a command + call + register + call-> doesnt work then works :D |DONE
 * */

test('I register a command and call it', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a register command for broadband command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockBroadband");
    await page.getByRole("button").click();

    //Check the history log for a success message
    await expect(page.getByTestId("Item 0")).toContainText("Successfully");
    await expect(page.getByLabel("Command input")).toBeEmpty();
    
    //Input a broadband command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockBroadband California Orange_County");
    await page.getByRole("button").click();    

    //Check history log for successful call
    await expect(page.getByTestId("Table 1 row 0")).toContainText("Location: California");
    await expect(page.getByTestId("Table 1 row 1")).toContainText("Percent: 93");

})

test('I call a command without registering it first', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a load command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad stardata.csv");
    await page.getByRole("button").click();

    //Check the history log for an error message
    await expect(page.getByTestId("Item 0")).not.toContainText("Sucessfully");
    await expect(page.getByTestId("Item 0")).toContainText("Command not recognized")
    await expect(page.getByLabel("Command input")).toBeEmpty();
})

test('I call a command without registering it first, then register it', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();
    //Input a load command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad stardata.csv");
    await page.getByRole("button").click();

    //Check the history log for an error message
    await expect(page.getByTestId("Item 0")).not.toContainText("Sucessfully");
    await expect(page.getByTestId("Item 0")).toContainText("Command not recognized")
    await expect(page.getByLabel("Command input")).toBeEmpty();

    //Register load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register mockLoad");
    await page.getByRole("button").click();

    //Call load
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("mockLoad stardata.csv");
    await page.getByRole("button").click();

    //Check history log for success
    await expect(page.getByTestId("Item 2")).toContainText("loaded stardata.csv");
    await expect(page.getByLabel("Command input")).toBeEmpty();
})

test('I register a command, then check to see if it enters the list of available commands', async ({ page }) => {
    await expect(page.getByLabel('Command input')).toBeVisible();

    //Register the load command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register load");
    await page.getByRole("button").click();

    await expect(page.getByTestId('Item 0')).toBeVisible();
    await expect(page.getByTestId('Item 0')).toContainText('Successfully');

    //Register the registry command
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("register registry");
    await page.getByRole("button").click();

    await expect(page.getByTestId('Item 1')).toBeVisible();
    await expect(page.getByTestId('Item 1')).toContainText('Successfully');

    //Check Registry
    await page.getByLabel("Command input").click();
    await page.getByLabel("Command input").fill("registry");
    await page.getByRole("button").click();

    await expect(page.getByTestId('Table 2 row 0 entry 0')).toBeVisible();
    await expect(page.getByTestId('Table 2 row 0 entry 0')).toContainText('register');
    await expect(page.getByTestId('Table 2 row 0 entry 1')).toContainText('load');
    await expect(page.getByTestId('Table 2 row 0 entry 2')).toContainText('registry');
})