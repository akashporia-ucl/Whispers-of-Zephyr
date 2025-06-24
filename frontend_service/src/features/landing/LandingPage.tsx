import React, { useEffect, useRef } from "react";
import gsap from "gsap";
import ScrollTrigger from "gsap/ScrollTrigger";
import "./LandingPage.css";

gsap.registerPlugin(ScrollTrigger);

const LandingPage: React.FC = () => {
    const sectionsRef = useRef<HTMLDivElement[]>([]);

    useEffect(() => {
        sectionsRef.current.forEach((section, index) => {
            gsap.fromTo(
                section,
                {
                    opacity: 0,
                    y: 50,
                },
                {
                    opacity: 1,
                    y: 0,
                    scrollTrigger: {
                        trigger: section,
                        start: "top 80%",
                        end: "top 30%",
                        scrub: true,
                    },
                }
            );
        });
    }, []);

    return (
        <div className="landing-root">
            {[
                {
                    title: "Introducing the Next Generation",
                    subtitle: "A smartwatch built for performance and elegance",
                },
                {
                    title: "Every Detail Refined",
                    subtitle:
                        "Smoother edges. Sharper display. Stronger build.",
                },
                {
                    title: "Health. Connected.",
                    subtitle:
                        "Track your vitals. Stay ahead of your wellbeing.",
                },
                {
                    title: "Unmatched Performance",
                    subtitle: "More power. More precision. More you.",
                },
                {
                    title: "It’s not just a watch.",
                    subtitle: "It’s a companion for your daily life.",
                },
            ].map((content, i) => (
                <section
                    className="landing-section"
                    key={i}
                    ref={(el) => {
                        if (el) sectionsRef.current[i] = el;
                    }}
                >
                    <h1>{content.title}</h1>
                    <h2>{content.subtitle}</h2>
                </section>
            ))}
        </div>
    );
};

export default LandingPage;
