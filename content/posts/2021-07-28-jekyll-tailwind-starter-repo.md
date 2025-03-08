---
slug: jekyll-tailwind-starter-repo
layout: post-toc
title: "Jekyll and Tailwind CSS with Minimal Effort"
description: Rapidly go from zero to deploy with Jekyll and Tailwind CSS.
summary: Rapidly go from zero to deploy with Jekyll and Tailwind CSS.
image: https://i.imgur.com/0q62Vcv.png
tags: [Jekyll, TailwindCSS]
last-updated: 2022-01-06
redirect-from:
  - /2021/07/28/jekyll-tailwind-starter-repo
---

Rapidly go from zero to deploy with [Jekyll](https://jekyllrb.com/) and
[Tailwind CSS](https://tailwindcss.com/) by cloning
[this repo](https://github.com/stefcoetzee/jekyll-tailwind).
Read on to learn more.

## Jekyll, Really?

Jekyll is the original static site generator (SSG).
While JavaScript-powered SSGs are all the rage nowadays, I started off with
Jekyll-powered GitHub Pages as part of working through Michael Hartl’s
[suite of web development courses](https://www.learnenough.com/).
(Highly recommended if you’re interested in fullstack web development, but unsure where to start.)

I’ve since discovered Netlify and moved over.
Call me old fashioned, but I still like good ol’ Jekyll.
(It remained a [popular choice](https://www.netlify.com/blog/2021/06/02/10-static-site-generators-to-watch-in-2021/#jekyll)
in 2021, FWIW.)
To install Jekyll, follow the appropriate instructions for your operating system
[here](https://jekyllrb.com/docs/installation/).

## A Design System for Recovering Perfectionists

More recently, Adam Wathan and Steve Schoger took the frontend world by storm
with Tailwind CSS.
I quite like the idea of a consistent design system within which to create a layout.
The perfectionist in me can rest easy, knowing there is someone who knows a great
deal more about these things spending a great deal more time on building
(and maintaining!) a complete system.
I get to gratefully use the end result.

HTML littered with utility class names isn’t for everyone, but I don’t mind it.
It’s just not the hill I’m gonna die on.
The speed and ease with which it allows one to create and update designs is great.
As an added bonus, every Tailwind CSS project doesn’t look the same just because
of the shared design system approach.
You can customize Tailwind CSS extensively to communicate the look and feel you’d like.
Just try not to use Inter as your primary typeface.

## Just in Time

Given the manner in which Jekyll processes site contents, throwing Tailwind CSS
into the mix used to lead to reload times in excess of five seconds.
(For save-triggered reloads, use the `--livereload` option when serving your site.)
That could be a bit of a drag on development.

<del>Can Olcer made a simple, effective
[suggestion](https://canolcer.com/post/jekyll-and-tailwind/): How about _not_
processing the Tailwind files on every reload?
Those files stay the same most of the time, and you could always include them if you’d like.
Great suggestion!</del>

Adam and company added on-demand style generation in Tailwind CSS v2.1,
through what they call [Just-in-Time Mode](https://tailwindcss.com/docs/just-in-time-mode) (JIT).
This didn’t work with Jekyll until [September 2021](https://github.com/mhanberg/jekyll-postcss/pull/32).

<del>To enable JIT, add the following [configuration](https://tailwindcss.com/docs/just-in-time-mode#enabling-jit-mode)
to `tailwind.config.js`:</del>

[Irrelevant code block removed.]

JIT is [enabled by default in Tailwind CSS v3](https://tailwindcss.com/blog/tailwindcss-v3#just-in-time-all-the-time).

Thanks to the [work done by Tristan Dunn](https://tristandunn.com/journal/jekyll-with-tailwindcss-jit-mode/),
[disable caching](https://tristandunn.com/journal/jekyll-with-tailwindcss-jit-mode/)
for the `jekyll-postcss` plugin in `_config.yml`:

```yaml
# _config.yml

# ...
postcss:
  cache: false
# ...
```

The result is near-instantaneous reloading.
Combine that with a blank Jekyll site, sprinkle in some scripts for building and
serving our site, and we’re good to go.

## Getting Started

It’s easy to get started,
[try it out for yourself](https://github.com/stefcoetzee/jekyll-tailwind/).
Simply clone the repo:

```bash
git clone git@github.com:stefcoetzee/jekyll-tailwind your-site
```

Change your directory to the repo directory and run the setup script:

```bash
cd your-site
bin/setup
```

Serve your site at `http://localhost:4000`:

```bash
bin/start
```

Rip out the placeholder code and have at it.
I find mimicking existing designs you like is a great way to kick the tires with
a utility-first CSS framework like Tailwind CSS.

Building for production is similarly straightforward:

```bash
bin/build-prod
```

Specify that as build command in your deployment settings.
