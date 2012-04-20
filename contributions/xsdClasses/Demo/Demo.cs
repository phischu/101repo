﻿using System.IO;
using System.Xml.Serialization;
using NUnit.Framework;

namespace xsdClasses
{
    [TestFixture]
    public class Demo
    {
        private Company Company
        {
            get
            {
                return xsdClasses.Serialization.Deserialize<Company>("AcmeCorp.xml");
            }
        }

        [Test]
        public void Deserialization()
        {
            Assert.AreEqual("AcmeCorp", Company.Name);
        }

        [Test]
        public void Serialization()
        {
            xsdClasses.Serialization.Serialize("AcmeCorpSerialized.xml", Company);
        }

        [Test]
        public void Total()
        {
            var total = Operations.Total(Company);
            Assert.AreEqual(399747.0, total);
        }

        [Test]
        public void CutAcmeCorp()
        {
            var AcmeCorp = xsdClasses.Serialization.Deserialize<Company>("AcmeCorp.xml");
            xsdClasses.Serialization.Serialize("AcmeCorpCut.xml", Company);
        }

        [Test]
        public void TotalAcmeCorp()
        {
            var AcmeCorp = xsdClasses.Serialization.Deserialize<Company>("AcmeCorp.xml");
            var total = Operations.Total(Company);
            using (var file = new System.IO.StreamWriter("AcmeCorpTotal.txt"))
            {
                file.WriteLine(total);
            }
        }
    }
}
