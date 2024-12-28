---
slug: rails-viewcomponent-rspec-tailwind
layout: post-toc
title: Simplify Rails Views Using ViewComponents with Tailwind CSS and RSpec
description: Use ViewComponent in a Tailwind CSS-styled Rails app exercised with RSpec tests.
summary: Use ViewComponent in a Tailwind CSS-styled Rails app exercised with RSpec tests.
---

Use ViewComponent in a Tailwind CSS-styled Rails app exercised with RSpec
tests.

After following this guide,
you’ll be able to confidently develop Rails app frontends with reusable components.

## Getting Started

I’ve taken the liberty of creating an example Rails 7 app using Tailwind CSS for
styling and RSpec for testing.
If you’d like, you can get that
[here](https://github.com/stefcoetzee/rails-viewcomponent-rspec-tailwind).
The end result is available on the `main` branch.
To restore the repo to its pre-ViewComponent state,
check out the `start-here` branch.

Quick aside: You can easily generate a new Rails 7 app with
[rails-new.stefcoetzee.com](https://rails-new.stefcoetzee.com).
My go-to these days:

```bash
rails new --database=postgresql --skip-test --css=tailwind
```

The example app we’re gonna run with allows for the creation of team rosters
listing players,
each with their respective positions and jersey numbers.
The app has no user model, just `Team` and `Athlete` (i.e. player) models.
To fill the app with example data, run

```bash
bin/rails db:seed
```

Fire up the app with `bin/dev` and visit `localhost:3000`.
You should see something similar to the following view:

<figure>
  <img src="https://i.imgur.com/m52hdxZ.png"
       alt="application root page"
       class="border border-gray-400">
  <figcaption class="text-center">
    Application root page.
  </figcaption>
</figure>

We can select a team to view its players:

<figure>
  <img src="https://i.imgur.com/ZWkf2di.png"
       alt="team page"
       class="border border-gray-400">
  <figcaption class="text-center">
    Team roster page.
  </figcaption>
</figure>

Select a player to view its details page,
which includes links that allow for editing and deletion of said player:

<figure>
  <img src="https://i.imgur.com/qm1aTpC.png"
       alt="player page"
       class="border border-gray-400">
  <figcaption class="text-center">
    Player details page.
  </figcaption>
</figure>

To exercise the app with RSpec, run `bundle exec rspec`.

With the preliminaries out of the way,
we can move on to the interesting stuff:
creating reusable view components using the `view_component` gem.
(I know, I know.
There’s a little linguistic gymnastics going on here.
Best I can tell,
ViewComponents are a specific kind of view component,
implemented by the `view_component` gem.)

## Refactor Views with ViewComponents

Check out
[viewcomponent.org](https://viewcomponent.org/)
for all things ViewComponents.
Joel Hawksley’s
[RailsConf 2019 talk](https://www.youtube.com/watch?v=y5Z5a6QdA-M)
serves as a great companion to the main site.

Let’s start off by creating and checking out a new branch:

```bash
git branch add-view-components
git checkout add-view-components
```

Add `gem "view_component", "~> 2.0"` to the Gemfile and run `bundle install`.

<!-- Add `rails_helper.rb` configuration -->

The [docs](https://viewcomponent.org/guide/testing.html#rspec-configuration)
provide some helpful instructions for configuring our app to make use of
helpers for components.
Add that as well:

```ruby
# spec/rails_helper.rb

...
# Add additional requires below this line. Rails is not loaded until this point!
require "view_component/test_helpers"
require "capybara/rspec"
...
RSpec.configure do |config|
...
  config.include ViewComponent::TestHelpers, type: :component
  config.include Capybara::RSpecMatchers, type: :component
end
...
```

Commit:

```bash
git commit -am "Install view_component and configure RSpec for view components"
```

To generate our first ViewComponent,
run `bin/rails generate component Application`.
Subsequent ViewComponents will automatically inherent from `ApplicationComponent`.
We add its view template for completeness’ sake,
but it won’t ever be called:

```erb
<%# app/components/application_component.html.erb %>

<div>
  <%= content %>
</div>

```

Clicking through the app,
we see all pages have titles of some kind.
Since we’d like the styling to be the same across all of them,
this a perfect use case for a reusable component.
Let’s generate a ViewComponent for page titles with
`bin/rails generate component PageTitle title`.

Open up `app/components/page_title_component.html.erb` and change the contents
to:

```erb
<%# app/components/page_title_component.html.erb %>

<div class="text-xl font-bold">
  <%= @title %>
</div>

```

`PageTitleComponent` instances can now replace duplications of the same styling
wherever we want a page title.

Page titles normally form part of a page header,
which—as I’m sure you’ve guessed—can be components as well.
Generate the next ViewComponent with
`bin/rails generate component PageHeader`.
Change the contents of the generated file
`app/components/page_header_component.html.erb` to:

```erb
<%# app/components/page_header_component.html.erb %>

<div class="pb-2 border-b">
  <%= content %>
</div>

```

Page headers can now be replaced as follows
(starting with `app/views/teams/index.html.erb`):

```erb
<%# app/views/teams/index.html.erb %>

<div class="mb-2">
  <%= render PageHeaderComponent.new do %>
    <%= render PageTitleComponent.new(title: "Teams") %>
  <% end %>
</div>
...
```

You’ll notice I’ve added padding to the component,
but kept margin spacing in the view itself.
I prefer this,
as margin is context specific,
whereas component padding will be uniform across the application.

Refresh `localhost:3000` and ... what gives?!
Tailwind doesn’t yet know to respond to changes made in the `./app/components/`
directory.
Let’s address that:

```js
// ./config/tailwind.config.js

...
module.exports = {
  content: [
    ...
    './app/views/**/*',
    './app/components/**/*'
  ]
  ...
}
...
```

This time around refreshing does yield the results we were hoping for.

Continuing on to the team show view,
we encounter a wrinkle:
there are edit and delete links in the header.
These also appear on individual players’ details page.
You know the drill:

```bash
bin/rails generate component EditLink href text
bin/rails generate component DeleteLink href text
```

Their respective `.html.erb` files follow below:

```erb
<%# app/components/edit_link_component.html.erb %>

<%= link_to @href do %>
  <div class="flex items-center space-x-1 text-gray-600 transition duration-300
              border-b border-white max-w-fit hover:border-b hover:border-gray-400">
    <div>
      <svg class="w-4" fill="none" stroke="currentColor"
        viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536
                 3.536L6.5 21.036H3v-3.572L16.732 3.732z">
        </path>
      </svg>
    </div>
    <div>
      Edit <%= @text %>
    </div>
  </div>
<% end %>

```

```erb
<%# app/components/delete_link_component.html.erb %>

<%= link_to @href, data: {
      turbo_method: :delete,
      turbo_confirm: "Are you sure?"
    } do %>
  <div class="flex items-center space-x-1 text-gray-600 duration-300 border-b
            border-white ransition max-w-fit hover:border-b hover:border-gray-400">
    <div>
      <svg class="w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5
                 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16">
        </path>
      </svg>
    </div>
    <div>
      Delete <%= @text %>
    </div>
  </div>
<% end %>

```

Replace all lines pertaining to the page header in
`app/views/teams/show.html.erb` with the following:

```erb
<%# app/views/teams/show.html.erb %>

...
<div class="mb-2">
  <%= render PageHeaderComponent.new do %>
    <div class="mb-1">
      <%= render PageTitleComponent.new(title: @team.name) %>
    </div>
    <div class="flex space-x-3">
      <%= render EditLinkComponent.new(href: edit_team_path(@team), text: "team") %>
      <%= render DeleteLinkComponent.new(href: team_path(@team), text: "team") %>
    </div>
  <% end %>
</div>
...
```

As mentioned,
there are edit and delete links in `app/views/athletes/show.html.erb` as well.
Let’s update those while we’re at it:

```erb
<%# app/views/athletes/show.html.erb %>

...
<div class="flex-col space-y-2">
  <%= render EditLinkComponent.new(
        href: edit_athlete_path(@athlete),
        text: "player details"
      ) %>
  <%= render DeleteLinkComponent.new(href: athlete_path(@athlete), text: "player") %>
</div>

```

Reloading the page confirms all is well.
Capture changes made with a commit.

```bash
git add .
git commit -m "Add page header and title components, edit and delete components"
```

What else might be a good candidate for components?
Those navigation links at the top of the page look like the kind of thing
best contained in a ViewComponent.
Generate `BackNavigationComponent` and populate
`app/components/back_navigation_component.html.erb` appropriately:

```erb
<%# app/components/back_navigation_component.html.erb %>

<%= link_to @href do %>
  <div class="flex items-center space-x-1 text-gray-600 transition duration-300
              border-b border-white max-w-fit hover:border-b hover:border-gray-400">
    <svg class="w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
        xmlns="http://www.w3.org/2000/svg">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M10 19l-7-7m0 0l7-7m-7 7h18">
      </path>
    </svg>
    <div>
      Back to <%= @text %>
    </div>
  </div>
<% end %>

```

Updating `app/views/athletes/show.html.erb` follows without much ado.
Let’s also add a `PageHeaderComponent` instance to the player details view,
complete with a back navigation link:

```erb
<%# app/views/athletes/show.html.erb %>

<div class="mb-2">
  <div class="mb-1">
    <%= render BackNavigationComponent.new(
          href: team_path(@athlete.team),
          text: "team"
    ) %>
  </div>
  <%= render PageHeaderComponent.new do %>
    <%= render PageTitleComponent.new(title: "Player details") %>
  <% end %>
</div>
...
```

Refactor the team show view to make use of our new `BackNavigationComponent`
class as well:

```erb
<%# app/views/teams/show.html.erb %>

<div class="mb-2">
  <%= render PageHeaderComponent.new do %>
    <div class="mb-1">
    <%= render BackNavigationComponent.new(
          href: teams_path,
          text: "teams"
    ) %>
    </div>
    ...
  <% end %>
</div>
...
```

Consolidate progress with a commit:

```bash
git add .
git commit -m "Add back navigation components and add page header to show athlete view"
```

Looking around,
those "Add [team/player]" links wouldn’t look bad as components.
Here’s what mine ended up as:

```erb
<%# app/components/add_link_component.html.erb %>

<%= link_to @href do %>
  <div class="flex items-center space-x-1 text-gray-600 duration-300 border-b
            border-white ransition max-w-fit hover:border-b hover:border-gray-400">
    <div>
      <svg class="w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round"
              stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6">
        </path>
      </svg>
    </div>
    <div>
      Add a <%= @text %>
    </div>
  </div>
<% end %>

```

Our app’s `new.html.erb` and `show.html.erb` pages also present some
opportunities to implement ViewComponents we’ve already created.
These follows trivially from previous examples.
For instance:

```erb
<%# app/views/teams/new.html.erb %>

<div class="mb-2">
  <%= render PageHeaderComponent.new do %>
    <div class="mb-1">
      <%= render BackNavigationComponent.new(href: teams_path, text: "teams") %>
    </div>
    <%= render PageTitleComponent.new(title: "Add a new team") %>
  <% end %>
</div>
...
```

Once these views have been refactored, make a commit:

```bash
git commit -am "Refactor controller new and edit views with components"
```

Given all these links components we’ve been creating,
I can’t help but wonder there isn’t a more fundamental link component waiting
to be created here. Here’s mine.

```erb
<%# app/components/link_component.html.erb %>

<div class="text-gray-600 transition duration-300 border-b border-white
              max-w-fit hover:border-b hover:border-gray-400">
  <%= content %>
</div>

```

I decided to leave the `link_to` methods in the various link components that
inherit from `LinkComponent` so that `DeleteComponent` can also inherit from it.
Refactoring, say, `EditLinkComponent` would look something like:

```erb
<%# app/components/edit_link_component.html.erb %>

<%= render LinkComponent.new do %>
  <%= link_to @href do %>
    <div class="flex items-center space-x-1">
      <svg class="w-4" fill="none" stroke="currentColor"
        viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536
                 3.536L6.5 21.036H3v-3.572L16.732 3.732z">
        </path>
      </svg>
      <div>
        Edit <%= @text %>
      </div>
    </div>
  <% end %>
<% end %>

```

`AddLinkComponent`, `BackNavigationComponent`, and `DeleteLinkComponent` can be
refactored similarly and are left as exercises.
`LinkComponent` has cut down on duplication of link styling across our app,
leaving the codebase more maintainable.
We might go one step further and introduce `IconLinkComponent` to standardize
across links making use of icons,
but I’d wager we’ve proved the point.

We commit:

```bash
git add .
git commit -m "Add LinkComponent and refactor AddLinkComponent, BackNavigationComponent, and EditLinkComponent to inherit from it"
```

Let’s run `bundle exec rspec` to determine the state of our test suite:

```bash
15 examples, 0 failures, 8 pending
```

No failures,
which means our refactoring effort went down without a hitch.
We’ll address those pending tests next.

## How to Test ViewComponents

The [docs](https://viewcomponent.org/guide/testing.html#best-practices)
recommend testing behaviour over testing specific methods.

This can be done by unit testing components using the `render_inline` test
helper and expecting specific content in the rendered output.
For example, to test that `LinkComponent` returns content,
we can simply test that we get back what we send to it:

```ruby
# spec/components/link_component_spec.rb

require "rails_helper"

RSpec.describe LinkComponent, type: :component do
  it "renders content" do
    test_text = "some content"
    render_inline(described_class.new.with_content(test_text))

    expect(rendered_component).to have_text(test_text)
  end
end

```

Our component passes this test, no problem.
`PageHeaderComponent` is tested similarly (as is `ApplicationComponent`).

How about those components containing actual links?
Let’s check out the spec for `AddLinkComponent`:

```ruby
# spec/components/add_link_component.rb

require "rails_helper"

RSpec.describe AddLinkComponent, type: :component do
  it "renders an add link" do
    test_url = "https://example.com"
    test_text = "player"
    render_inline(described_class.new(href: test_url, text: test_text))

    expect(rendered_component).to have_link "Add a #{test_text}", href: test_url
  end
end

```

Here we’re testing that the component returns a provided link with specific
text ("Add a player").
If we run `bundle exec rspec spec/components/add_link_component_spec.rb`,
we’ll get a passing test.
The other link components (back, delete, and edit) tests are structured similarly.

After writing tests for all of our components,
what is the status of our test suite?

```bash
15 examples, 0 failures
```

[Huzzah](https://en.wikipedia.org/wiki/Huzzah)!

<!-- Add outstanding controller tests (where edit actions fail),
     Consider putting the error messages in a component,
     since it's used often. -->

Time to bring those components home:

```bash
git commit-am "Add component specs"
git checkout main
git merge add-view-components
```

## Once More With Feeling

After running the full test suite,
a visit to the `simple_cov` index page (`coverage/index.html`) reveals that
some aspects of our controllers’ actions remain untested.
This is a great opportnity to go through the motions one last time.
We’ll end up adding form error components,
so it doesn’t hurt to name the branch after that:

```bash
git branch add-form-errors-component
git checkout add-form-errors-component
```

Update `spec/system/edit_teams_spec.rb` to:

```ruby
# spec/system/edit_teams_spec.rb

...
RSpec.describe "Existing teams", type: :system do
  ...
  it "can be edited with valid attributes" do
    ...
  end

  it "cannot be edited with invalid attributes" do
    click_link "Edit team"
    fill_in "Name", with: ""
    click_button "Update team"

    expect(page).to have_content "Team has not been updated."
    expect(page).to have_content "Name can't be blank"
  end
end

```

Initially, the second test above fails,
but we can start the work on correcting that with an update to `TeamsController`:

```ruby
# app/controllers/teams_controller.rb

...
def create
  @team = Team.new(team_params)
  if @team.save
    flash[:notice] = "Team has been created."
    redirect_to @team
  else
    flash[:notice] = "Team has not been created."
    render "new"
  end
end
...
```

Next, we turn our attention to that second expectation.
In the new team view,
you’ll notice the `form_with` block includes the following error-handling section:

```erb
<%# app/views/teams/new.html.erb %>

<%= form_with model: @team, data: { turbo: false } do |f| %>
  <% if @team.errors.any? %>
    <div>
      <%= pluralize(@team.errors.count, "error") %>
      prevented this team from being saved:
    </div>
    <ul>
      <% @team.errors.full_messages.each do |msg| %>
        <li>
          <%= msg %>
        </li>
      <% end %>
    </ul>
  <% end %>
  ...
<% end%>
```

Something like this section will be required in each view containing a form,
exactly the kind of thing abstracted away with a reusable component!
There’s no time like the present:

```bash
bin/rails generate component FormErrors resource descriptor
```

Replace the generated content of
`app/components/form_errors_component.html.erb` with:

```erb
<%# app/components/form_errors_component.html.erb %>

<div class="text-red-600">
  <div>
    <%= pluralize(@resource.errors.count, "error") %>
    prevented this <%= @descriptor %> from being saved:
  </div>
  <ul class="list-disc list-inside">
    <% @resource.errors.full_messages.each do |msg| %>
      <li>
        <%= msg %>
      </li>
    <% end %>
  </ul>
</div>

```

Back in `app/views/teams/new.html.erb`,
we can now simplify the `form_with` block:

```erb
<%# app/views/teams/new.html.erb %>

...
<%= form_with model: @team, data: { turbo: false } do |f| %>
  <% if @team.errors.any? %>
    <%= render FormErrorsComponent.new(resource: @team, descriptor: "team") %>
  <% end %>
  ...
<% end %>
```

This also applies to `app/views/teams/edit.html.erb`,
the addition of which leads to our test passing.

`form_with` blocks in our new and edit athlete views can be updated to include:

```erb
<% if @athlete.errors.any? %>
  <%= render FormErrorsComponent.new(
    resource: @athlete,
    descriptor: "player"
  ) %>
<% end %>
```

Be sure to expand the associated specs to cover failed create and edit attempts,
for instance:

```ruby
# spec/system/create_athletes_spec.rb

...
RSpec.describe "New athletes", type: :system do
  ...
  it "cannot be created with invalid attributes" do
    fill_in "First name", with: ""
    fill_in "Last name", with: ""
    fill_in "Position", with: ""
    fill_in "Jersey number", with: ""
    click_button "Add player"

    expect(page).to have_content "Player has not been added."
    expect(page).to have_content "First name can't be blank"
    expect(page).to have_content "Last name can't be blank"
    expect(page).to have_content "Position can't be blank"
    expect(page).to have_content "Jersey number can't be blank"
  end
end

```

This in turn will drive updates to `AthletesController`, for example:

```ruby
# app/controllers/athletes_controller.rb

...
def create
  @athlete = @team.athletes.new(athlete_params)
  if @athlete.save
    flash[:notice] = "Player has been added."
    redirect_to team_path(@team)
  else
    flash[:notice] = "Player has not been added."
    render "new"
  end
end
...
```

Last but not least,
we test `FormErrorsComponent`:

```ruby
# spec/components/form_errors_component_spec.rb

require "rails_helper"

RSpec.describe FormErrorsComponent, type: :component do
  let(:team) { FactoryBot.create(:team) }
  let(:player) { FactoryBot.create(:athlete, team: team) }

  it "renders a player's error list" do
    render_inline(described_class.new(resource: player, descriptor: "player"))

    expect(rendered_component).to(
      have_content("0 errors")
        .and have_content("prevented this player from being saved")
    )
  end
end

```

If we run `bundle exec rspec` once more, it’s nothing but net:

```bash
19 examples, 0 failures
```

Consolidate progress and merge back into `main`:

```bash
git commit -am "Refactor form errors to components and expand controllers to handle failed create and edit attempts"
git checkout main
git merge add-form-errors-components
```

## Closing Thoughts

This has been a thorough exploration of using ViewComponents to make
frontend development more ergonomic while providing a more consistent experience
across our applications.

Tailwind CSS provides a powerful, extensible design system that gains an
additional boost from reusable components.
Tests allow for refactoring to components to be a much more routine affair,
preventing regressions from slipping in under the radar.

What might you refactor to a ViewComponent?
Feel free to
[let me know on Twitter](https://twitter.com/stef_coetzee/status/1495023092875157506).
😃
